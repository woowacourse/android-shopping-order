package woowacourse.shopping.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.ProductNotFoundException
import woowacourse.shopping.feature.common.state.CartItemUiModel
import woowacourse.shopping.feature.common.state.ProductUiModel

sealed interface CartEvent {
    data class FatalError(
        val message: String,
    ) : CartEvent
}

data class CartUiState(
    val isLoading: Boolean = true,
    val page: Int = 1,
    val paginatedCartContents: List<CartItemUiModel> = emptyList(),
    val checkMap: Map<Long, Boolean> = emptyMap(),
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
    val isFirstPage: Boolean = true,
    val isLastPage: Boolean = true
)

class CartViewModel(
    private val initialPageSize: Int = 5,
    private val application: ShoppingApplication,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _event = Channel<CartEvent>(Channel.BUFFERED)
    val event: Flow<CartEvent> = _event.receiveAsFlow()

    lateinit var cartRepository: CartRepository

    init {
        viewModelScope.launch {
            val appDependencies = application.appDependenciesDeferred.await()
            cartRepository = appDependencies.cartRepository
        }
        initialLoading()
    }

    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cart = getCart()
            val cartContents = pagination(
                page = 1,
            )
            val checkMap: Map<Long, Boolean> =
                cartContents.map { it.contentId }.associateWith { false }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    paginatedCartContents = cartContents,
                    checkMap = checkMap,
                    isFirstPage = it.page == 1,
                    isLastPage = it.page >= lastPage(initialPageSize)
                )
            }
        }
    }

    private suspend fun getCart(): Cart {
        val cart = cartRepository.loadCart()
        return cart
    }

    private fun lastPage(pageSize: Int): Int {
        val size = cart.cartContentsSizeOf()
        if (size == 0) return 1
        return (size + pageSize - 1) / pageSize
    }

    fun moveToPreviousPage() {
        viewModelScope.launch {
            val page = uiState.value.page - 1
            val cartContents = pagination(page)
            val checkMap: Map<Long, Boolean> =
                cartContents.map { it.contentId }.associateWith { false }
            _uiState.update {
                it.copy(
                    page = page,
                    paginatedCartContents = cartContents,
                    checkMap = checkMap,
                    isFirstPage = page == 1,
                    isLastPage = page >= lastPage(initialPageSize)
                )
            }
        }
    }

    fun moveToNextPage() {
        viewModelScope.launch {
            val page = uiState.value.page + 1
            val cartContents = pagination(page)
            val checkMap: Map<Long, Boolean> =
                cartContents.map { it.contentId }.associateWith { false }
            _uiState.update {
                it.copy(
                    page = page,
                    paginatedCartContents = cartContents,
                    checkMap = checkMap,
                    isFirstPage = page == 1,
                    isLastPage = page >= lastPage(initialPageSize),
                )
            }
        }
    }

    private suspend fun pagination(
        page: Int,
        pageSize: Int = 5,
    ): List<CartItemUiModel> {
        val cartContents = cartRepository
            .pagination(page - 1, pageSize)
            .map(::toCartItemUiModel)
        return cartContents
    }

    fun increase(contentId: Long) = guardFatal {
        val product = cart.cartContents.firstOrNull { it.id == contentId }?.product
            ?: throw ProductNotFoundException(contentId)

        viewModelScope.launch {
            cartRepository.increase(product)
            cart = getCart()
            val cartContents = pagination(uiState.value.page)
            _uiState.update {
                it.copy(
                    paginatedCartContents = cartContents,
                    totalPrice = it.totalPrice + if (it.checkMap[contentId] == true) product.priceAmount() else 0,
                )
            }
        }
    }

    fun decrease(contentId: Long) = guardFatal {
        val product = cart.cartContents.firstOrNull { it.id == contentId }?.product
            ?: throw ProductNotFoundException(contentId)

        viewModelScope.launch {
            cartRepository.decrease(product.id)
            cart = getCart()
            val cartContents = pagination(uiState.value.page)
            val page = maxOf(
                1,
                if (cartContents.isEmpty()) uiState.value.page - 1 else uiState.value.page
            )
            _uiState.update {
                it.copy(
                    paginatedCartContents = cartContents.ifEmpty { pagination(page - 1) },
                    totalPrice = it.totalPrice - if (it.checkMap[contentId] == true) product.priceAmount() else 0,
                    isFirstPage = page == 1,
                    isLastPage = page >= lastPage(initialPageSize),
                    page = page
                )
            }
        }
    }

    private inline fun guardFatal(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            _event.trySend(
                CartEvent.FatalError(
                    e.message
                        ?: "알 수 없는 오류가 발생했습니다.",
                ),
            )
        }
    }

    fun deleteCartItem(productId: Long) {
        viewModelScope.launch {
            cart = getCart()
            val contentId = cart.cartContents.firstOrNull { it.productId == productId }?.id ?: 0
            cartRepository.remove(productId)

            var cartContents = pagination(uiState.value.page)

            val page = maxOf(
                1,
                if (cartContents.isEmpty()) uiState.value.page - 1 else uiState.value.page
            )
            if (cartContents.isEmpty() && page != uiState.value.page) {
                cartContents = pagination(page)
            }
            cart = getCart()
            val newCheckMap = _uiState.value.checkMap.toMutableMap()
            newCheckMap.remove(contentId)

            val newTotalPrice = cart.cartContents
                .filter{ newCheckMap[it.id] == true }
                .sumOf { it.product.priceAmount() * it.quantity }

            val newTotalCount = newCheckMap.filter { it.value }.size

            _uiState.update {
                it.copy(
                    paginatedCartContents = cartContents.ifEmpty { pagination(page - 1) },
                    page = page,
                    isFirstPage = page == 1,
                    isLastPage = page >= lastPage(initialPageSize),
                    checkMap = newCheckMap.toMap(),
                    totalCount = newTotalCount,
                    totalPrice = newTotalPrice
                )
            }
        }
    }

    fun toCartItemUiModel(cartContent: CartContent): CartItemUiModel {
        val product = cartContent.product
        return CartItemUiModel(
            contentId = cartContent.id,
            productUiModel = ProductUiModel(
                name = product.name,
                price = product.priceAmount(),
                imageUrl = product.imageUrl,
                id = product.id,
                quantity = cartContent.quantity,
            ),
        )
    }

    fun cartItemCheck(productId: Long) {
        val checkMap = _uiState.value.checkMap.toMutableMap()
        checkMap[productId] = checkMap[productId]?.not()
            ?: false
        val cartItemUiModels = _uiState.value.paginatedCartContents
        val totalPrice =
            cartItemUiModels.filter { checkMap[it.contentId] == true }
                .sumOf { it.productUiModel.price * it.productUiModel.quantity }
        _uiState.update {
            it.copy(
                checkMap = checkMap.toMap(),
                totalPrice = totalPrice,
                totalCount = checkMap.filter { it.value }.size
            )
        }
    }

    fun totalCheck() {
        val productUiModels = _uiState.value.paginatedCartContents
        val totalPrice =
            productUiModels.sumOf { it.productUiModel.price * it.productUiModel.quantity }

        val check = _uiState.value.checkMap.all { it.value }.not()
        val newCheckMap = productUiModels.map { it.contentId }.associateWith { check }

        _uiState.update { it.copy(checkMap = newCheckMap.toMap(), totalPrice = totalPrice) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                CartViewModel(5, app)
            }
        }
    }
}
