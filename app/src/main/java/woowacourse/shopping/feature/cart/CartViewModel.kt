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
import woowacourse.shopping.feature.common.state.ProductUiModel

sealed interface CartEvent {
    data class FatalError(
        val message: String,
    ) : CartEvent
}

data class CartUiState(
    val isLoading: Boolean = true,
    val page: Int = 1,
    val paginatedCartContents: List<ProductUiModel> = emptyList(),
    val checkMap: Map<String, Boolean> = emptyMap(),
    val totalPrice: Int = 0,
)

class CartViewModel(
    private val initialPageSize: Int = 5,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _event = Channel<CartEvent>(Channel.BUFFERED)
    val event: Flow<CartEvent> = _event.receiveAsFlow()

    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cart = getCart()
            val cartContents = pagination(
                page = 1,
            )
            val checkMap: Map<String, Boolean> = cartContents.map { it.id }.associateWith { false }
            _uiState.update { it.copy(isLoading = false, paginatedCartContents = cartContents, checkMap = checkMap) }
        }
    }

    private suspend fun getCart(): Cart {
        val cart = cartRepository.loadCart()
        return cart
    }

    fun isStartPage(): Boolean = uiState.value.page == 1

    fun isEndPage(): Boolean = uiState.value.page >= lastPage(initialPageSize)

    private fun lastPage(pageSize: Int): Int {
        val size = cart.cartContentsSizeOf()
        if (size == 0) return 1
        return (size + pageSize - 1) / pageSize
    }

    fun moveToPreviousPage() {
        val page = uiState.value.page - 1
        _uiState.update {
            it.copy(
                page = page,
            )
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val cartContents = pagination(page)
            val checkMap: Map<String, Boolean> = cartContents.map { it.id }.associateWith { false }
            _uiState.update { it.copy(isLoading = false, paginatedCartContents = cartContents, checkMap = checkMap) }
        }
    }

    fun moveToNextPage() {
        val page = uiState.value.page + 1
        _uiState.update {
            it.copy(
                page = page,
            )
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val cartContents = pagination(page)
            val checkMap: Map<String, Boolean> = cartContents.map { it.id }.associateWith { false }
            _uiState.update { it.copy(isLoading = false, paginatedCartContents = cartContents, checkMap = checkMap) }
        }
    }

    private suspend fun pagination(
        page: Int,
        pageSize: Int = 5,
    ): List<ProductUiModel> {
        val cartContents = cartRepository
            .pagination(page - 1, pageSize)
            .map(::toProductUiModel)
        return cartContents
    }

    fun increase(productId: String) = guardFatal {
        val product = cart.getProductList().firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository.increase(product)
            cart = getCart()
            val cartContents = pagination(uiState.value.page)
            val checkMap: Map<String, Boolean> = cartContents.map { it.id }.associateWith { false }
            _uiState.update { it.copy(isLoading = false, paginatedCartContents = cartContents, checkMap = checkMap) }
        }
    }

    fun decrease(productId: String) = guardFatal {
        cart.getProductList().firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository.decrease(productId)
            cart = getCart()
            val cartContents = pagination(uiState.value.page)
            val checkMap: Map<String, Boolean> = cartContents.map { it.id }.associateWith { false }
            _uiState.update { it.copy(isLoading = false, paginatedCartContents = cartContents, checkMap = checkMap) }
        }
    }

    private inline fun guardFatal(block: () -> Unit) {
        try {
            block()
        } catch (e: ProductNotFoundException) {
            _event.trySend(
                CartEvent.FatalError(
                    e.message
                        ?: "알 수 없는 오류가 발생했습니다.",
                ),
            )
        }
    }

    fun deleteCartItem(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository.remove(id)
            val cartContents = pagination(uiState.value.page)
            val checkMap: Map<String, Boolean> = cartContents.map { it.id }.associateWith { false }
            _uiState.update { it.copy(isLoading = false, paginatedCartContents = cartContents, checkMap = checkMap) }
        }
    }

    fun toProductUiModel(cartContent: CartContent): ProductUiModel {
        val product = cartContent.product
        return ProductUiModel(
            name = product.name,
            price = product.priceAmount(),
            imageUrl = product.imageUrl,
            id = product.id,
            quantity = cartContent.quantity,
        )
    }

    fun cartItemCheck(productId: String) {
        val checkMap = _uiState.value.checkMap.toMutableMap()
        checkMap[productId] = checkMap[productId]?.not()
            ?: false
        val productUiModels = _uiState.value.paginatedCartContents
        val totalPrice = productUiModels.filter { checkMap[it.id] == true }.sumOf { it.price * it.quantity }
        _uiState.update { it.copy(checkMap = checkMap.toMap(), totalPrice = totalPrice) }
    }

    fun totalCheck() {
        val productUiModels = _uiState.value.paginatedCartContents
        val totalPrice = productUiModels.sumOf { it.price * it.quantity }

        val check = _uiState.value.checkMap.all { it.value }.not()
        val newCheckMap = productUiModels.map { it.id }.associateWith { check }

        _uiState.update { it.copy(checkMap = newCheckMap.toMap(), totalPrice = totalPrice) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                CartViewModel(5, app.cartRepository)
            }
        }
    }
}
