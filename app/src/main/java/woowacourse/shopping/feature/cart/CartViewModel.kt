package woowacourse.shopping.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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


data class CartUiState(
    val isLoading: Boolean = true,
    val page: Int = 1,
    val paginatedCartContents: List<CartItemUiModel> = emptyList(),
    val checkMap: Map<Long, Boolean> = emptyMap(),
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
    val isFirstPage: Boolean = true,
    val isLastPage: Boolean = true,
)

class CartViewModel(
    private val initialPageSize: Int = 5,
    private val application: ShoppingApplication,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _event = Channel<CartEvent>(Channel.BUFFERED)
    val event: Flow<CartEvent> = _event.receiveAsFlow()

    private val _removeEvent = MutableSharedFlow<RemoveEvent>()
    val removeEvent: Flow<RemoveEvent> = _removeEvent.asSharedFlow()

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
            cart = cartRepository.loadCart()
            val cartContents = pagination(
                page = 1,
            )
            val checkMap: Map<Long, Boolean> =
                cart.cartContents.map { it.id }.associateWith { true }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    paginatedCartContents = cartContents,
                    checkMap = checkMap,
                    totalPrice = cart.cartContents
                        .filter { checkMap[it.id] == true }
                        .sumOf { it.product.priceAmount() * it.quantity },
                    totalCount = checkMap.count { it.value },
                    isFirstPage = it.page == 1,
                    isLastPage = it.page >= lastPage(initialPageSize),
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
            _uiState.update {
                it.copy(
                    page = page,
                    paginatedCartContents = cartContents,
                    isFirstPage = page == 1,
                    isLastPage = page >= lastPage(initialPageSize),
                )
            }
        }
    }

    fun moveToNextPage() {
        viewModelScope.launch {
            val page = uiState.value.page + 1
            val cartContents = pagination(page)
            _uiState.update {
                it.copy(
                    page = page,
                    paginatedCartContents = cartContents,
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
        val cartContents =
            cartRepository
                .pagination(page - 1, pageSize)
                .map(::toCartItemUiModel)
        return cartContents
    }

    fun increase(contentId: Long) =
        guardFatal {
            val product =
                cart.cartContents.firstOrNull { it.id == contentId }?.product
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

    fun decrease(contentId: Long) =
        guardFatal {
            val product =
                cart.cartContents.firstOrNull { it.id == contentId }?.product
                    ?: throw ProductNotFoundException(contentId)

            viewModelScope.launch {
                cartRepository.decrease(product.id)

                val cartContents = pagination(uiState.value.page)
                val updateCartContents = getCartContents(cartContents)
                val page = getPage(cartContents)

                cart = getCart()
                val updateContentKeys = cart.cartContents.map { it.id }
                val newCheckMap = _uiState.value.checkMap.filter{ it.key in updateContentKeys }.toMap()

                _uiState.update {
                    it.copy(
                        paginatedCartContents = updateCartContents,
                        checkMap = newCheckMap,
                        totalPrice = cart.cartContents
                            .filter { newCheckMap[it.id] == true }
                            .sumOf { it.product.priceAmount() * it.quantity },
                        totalCount = newCheckMap.count { it.value },
                        isFirstPage = page == 1,
                        isLastPage = page >= lastPage(initialPageSize),
                        page = page,
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
            val contentId = cart.cartContents.firstOrNull { it.productId == productId }?.id ?: 0
            cartRepository.remove(contentId)
            val cartContents = pagination(uiState.value.page)
            val updateCartContents = getCartContents(cartContents)
            val page = getPage(cartContents)

            cart = getCart()
            val updateContentKeys = cart.cartContents.map { it.id }
            val newCheckMap = _uiState.value.checkMap.filter{ it.key in updateContentKeys }.toMap()

            _uiState.update {
                it.copy(
                    paginatedCartContents = updateCartContents,
                    page = page,
                    isFirstPage = page == 1,
                    isLastPage = page >= lastPage(initialPageSize),
                    checkMap = newCheckMap,
                    totalCount = newCheckMap.count { it.value },
                    totalPrice = cart.cartContents
                        .filter { newCheckMap[it.id] == true }
                        .sumOf { it.product.priceAmount() * it.quantity },
                )
            }

            _removeEvent.emit(RemoveEvent("해당 상품이 삭제되었습니다."))
        }
    }

    fun toCartItemUiModel(cartContent: CartContent): CartItemUiModel {
        val product = cartContent.product
        return CartItemUiModel(
            contentId = cartContent.id,
            productUiModel =
                ProductUiModel(
                    name = product.name,
                    price = product.priceAmount(),
                    imageUrl = product.imageUrl,
                    id = product.id,
                    quantity = cartContent.quantity,
                ),
        )
    }

    fun cartItemCheck(contentId: Long) {
        val checkMap = _uiState.value.checkMap.toMutableMap()
        checkMap[contentId] = checkMap[contentId]?.not()
            ?: false
        val totalPrice =
            cart.cartContents
                .filter { checkMap[it.id] == true }
                .sumOf { it.product.priceAmount() * it.quantity }
        _uiState.update {
            it.copy(
                checkMap = checkMap.toMap(),
                totalPrice = totalPrice,
                totalCount = checkMap.filter { it.value }.size,
            )
        }
    }

    fun totalCheck() {
        val isAllChecked = _uiState.value.checkMap.all { it.value }
        val nextCheckState = !isAllChecked

        val newCheckMap = _uiState.value.checkMap.keys.associateWith { nextCheckState }

        val totalPrice = if (nextCheckState) {
            cart.cartContents.sumOf {
                it.product.priceAmount() * it.quantity
            }
        } else {
            0
        }
        val totalCount = if (nextCheckState) newCheckMap.size else 0

        _uiState.update {
            it.copy(
                checkMap = newCheckMap.toMap(),
                totalPrice = totalPrice,
                totalCount = totalCount
            )
        }
    }

    private fun getPage(cartContents: List<CartItemUiModel>): Int = maxOf(
        1,
        if (cartContents.isEmpty()) uiState.value.page - 1 else uiState.value.page,
    )

    private suspend fun getCartContents(cartContents: List<CartItemUiModel>): List<CartItemUiModel> {
        val page = getPage(cartContents)

        return if (cartContents.isEmpty() && page != uiState.value.page) {
            pagination(page)
        } else {
            cartContents
        }
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    CartViewModel(5, app)
                }
            }
    }
}


sealed interface CartEvent {
    data class FatalError(
        val message: String,
    ) : CartEvent
}

data class RemoveEvent(val message: String)
