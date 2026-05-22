package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.ui.cart.CartEvent.*
import java.io.IOException

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvent>()
    val event: SharedFlow<CartEvent> = _event.asSharedFlow()

    private val selectedCartItems = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    private val page = MutableStateFlow(0)

    init {
        observeCartUiState()
        refreshCartItems()
    }

    private fun observeCartUiState() {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                selectedCartItems,
                page,
            ) { cartItems, selectedItems, page ->
                _uiState.value.toUiState(
                    cartItems = cartItems,
                    selectedItems = selectedItems,
                    page = page,
                    pageSize = PAGE_SIZE,
                )
            }.collect { nextState ->
                _uiState.value = nextState

                if (nextState.page != page.value) {
                    page.value = nextState.page
                }
                if (nextState.selectedCartItems != selectedCartItems.value) {
                    selectedCartItems.value = nextState.selectedCartItems
                }
            }
        }
    }

    private fun refreshCartItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                cartRepository.refreshCartItems()
            } catch (_: IOException) {
                _uiState.update { it.copy(errorMessage = "상품 불러오기 실패") }
            } catch (_: HttpException) {
                _uiState.update { it.copy(errorMessage = "상품 불러오기 실패") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun nextPage() {
        page.update { it + 1 }
    }

    fun previousPage() {
        page.update { it - 1 }
    }

    fun deleteItem(cartId: String) {
        viewModelScope.launch {
            runCatching {
                cartRepository.deleteItem(cartId)
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _event.emit(DeleteCartItemFailure)
                } else {
                    throw throwable
                }
            }
        }
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            runCatching {
                cartRepository.setCartItem(productId, quantity = quantity)
            }.onFailure { throwable ->
                if (throwable is IOException || throwable is HttpException) {
                    _event.emit(UpdateCartItemFailure)
                } else {
                    throw throwable
                }
            }
        }
    }

    fun checkItem(cartItemId: String) {
        selectedCartItems.update { selectedItemsId ->
            if (cartItemId in selectedItemsId) {
                (selectedItemsId - cartItemId).toImmutableList()
            } else {
                (selectedItemsId + cartItemId).toImmutableList()
            }
        }
    }

    fun isAllSelectClick() {
        val cartItems = cartRepository.cartItems.value

        selectedCartItems.update {
            if (_uiState.value.isAllChecked) {
                persistentListOf()
            } else {
                cartItems.map { it.id }.toImmutableList()
            }
        }
    }

    fun order() {
        viewModelScope.launch {
            _event.emit(NavigateToRecommend)
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        fun provideFactory(cartRepository: CartRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(
                        cartRepository = cartRepository,
                    )
                }
            }
    }
}
