package woowacourse.shopping.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.feature.common.state.AppError
import woowacourse.shopping.feature.common.state.CartItemUiModel
import woowacourse.shopping.feature.common.state.ProductUiModel
import woowacourse.shopping.feature.common.state.toAppError

data class CartUiState(
    val isLoading: Boolean = true,
    val page: Int = 1,
    val paginatedCartContents: List<CartItemUiModel> = emptyList(),
    val checkMap: Map<String, Boolean> = emptyMap(),
    val totalPrice: Int = 0,
    val error: AppError? = null,
)

class CartViewModel(
    private val initialPageSize: Int = 5,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        launchCatching {
            _uiState.update { it.copy(isLoading = true, error = null) }
            cart = getCart()
            val cartContents = pagination(page = 1)
            applyContents(cartContents)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun launchCatching(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.toAppError()) }
            }
        }
    }

    private fun applyContents(cartContents: List<CartItemUiModel>) {
        val checkMap = mergeCheckMap(cartContents, _uiState.value.checkMap)
        val totalPrice = calculateTotalPrice(cartContents, checkMap)
        _uiState.update {
            it.copy(
                isLoading = false,
                paginatedCartContents = cartContents,
                checkMap = checkMap,
                totalPrice = totalPrice,
            )
        }
    }

    private fun mergeCheckMap(
        cartContents: List<CartItemUiModel>,
        previous: Map<String, Boolean>,
    ): Map<String, Boolean> = cartContents.associate { it.contentId to (previous[it.contentId] ?: false) }

    private fun calculateTotalPrice(
        cartContents: List<CartItemUiModel>,
        checkMap: Map<String, Boolean>,
    ): Int = cartContents
        .filter { checkMap[it.contentId] == true }
        .sumOf { it.productUiModel.price * it.productUiModel.quantity }

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
        _uiState.update { it.copy(page = page) }
        launchCatching {
            _uiState.update { it.copy(isLoading = true) }
            applyContents(pagination(page))
        }
    }

    fun moveToNextPage() {
        val page = uiState.value.page + 1
        _uiState.update { it.copy(page = page) }
        launchCatching {
            _uiState.update { it.copy(isLoading = true) }
            applyContents(pagination(page))
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

    fun increase(contentId: String) {
        launchCatching {
            _uiState.update { it.copy(isLoading = true) }
            val quantity = cart.cartContents.firstOrNull { it.id == contentId }?.quantity
                ?: return@launchCatching
            cartRepository.updateQuantity(contentId, quantity + 1)
            cart = getCart()
            applyContents(pagination(uiState.value.page))
        }
    }

    fun decrease(contentId: String) {
        launchCatching {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository.decrease(contentId)
            cart = getCart()
            applyContents(pagination(uiState.value.page))
        }
    }

    fun deleteCartItem(id: String) {
        launchCatching {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository.remove(id)
            cart = getCart()
            applyContents(pagination(uiState.value.page))
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

    fun cartItemCheck(productId: String) {
        val checkMap = _uiState.value.checkMap.toMutableMap()
        checkMap[productId] = checkMap[productId]?.not() ?: false
        val cartItemUiModels = _uiState.value.paginatedCartContents
        val totalPrice = calculateTotalPrice(cartItemUiModels, checkMap)
        _uiState.update { it.copy(checkMap = checkMap.toMap(), totalPrice = totalPrice) }
    }

    fun totalCheck() {
        val productUiModels = _uiState.value.paginatedCartContents
        val check = _uiState.value.checkMap.all { it.value }.not()
        val newCheckMap = productUiModels.associate { it.contentId to check }
        val totalPrice = calculateTotalPrice(productUiModels, newCheckMap)
        _uiState.update { it.copy(checkMap = newCheckMap, totalPrice = totalPrice) }
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
