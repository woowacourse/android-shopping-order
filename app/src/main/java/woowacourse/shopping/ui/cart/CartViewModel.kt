package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.ui.model.mapper.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getCartItemsByPage()
        }
        _uiState.update { it.copy(isLoading = false) }
    }

    private suspend fun getCartItemsByPage() {
        val cartResult =
            cartRepository.getCartItemsByPage(page = uiState.value.page, size = PAGE_SIZE)

        _uiState.update {
            it.copy(
                items =
                    cartResult.cartItems
                        .map { cartItem ->
                            cartItem.toUiModel()
                        }.toImmutableList(),
                isCanMoveNext = !cartResult.isLastPage,
                totalCartSize = cartRepository.getTotalCartItemQuantity(),
            )
        }
    }

    fun nextPage() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    page = uiState.value.page + 1
                )
            }
            getCartItemsByPage()
        }
    }

    fun previousPage() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    page = uiState.value.page - 1
                )
            }
            getCartItemsByPage()
        }
    }

    fun deleteItem(cartId: String) {
        viewModelScope.launch {
            cartRepository.deleteItem(cartId)

            getCartItemsByPage()
        }
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository.setCartItem(productId, quantity = quantity)

            getCartItemsByPage()
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        fun provideFactory(
            cartRepository: CartRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(
                        cartRepository = cartRepository,
                    )
                }
            }
    }
}


private data class CartPage(
    val items: List<CartItem>,
    val page: Int,
    val isCanMoveNext: Boolean,
)
