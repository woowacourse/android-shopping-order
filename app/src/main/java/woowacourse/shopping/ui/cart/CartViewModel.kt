package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CartRepository

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private var currentPage = 0

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch {
            _uiState.update { CartUiState.Loading }

            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values,
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                )
            }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            _uiState.update { CartUiState.Loading }
            cartRepository.remove(cartId)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values,
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                )
            }
        }
    }

    fun increase(cartId: Int) {
        viewModelScope.launch {
            val uiState = _uiState.value as? CartUiState.Success ?: return@launch

            val target = uiState.cartItems.find { it.id == cartId } ?: return@launch
            cartRepository.increase(cartId, target.quantity.value + 1)

            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values,
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                )
            }
        }
    }

    fun decrease(cartId: Int) {
        viewModelScope.launch {
            val uiState = _uiState.value as? CartUiState.Success ?: return@launch

            val target = uiState.cartItems.find { it.id == cartId } ?: return@launch
            cartRepository.decrease(cartId, target.quantity.value - 1)

            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values,
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                )
            }
        }
    }

    fun goToNextPage() {
        val current = _uiState.value as? CartUiState.Success ?: return
        if (!current.hasNext) return

        viewModelScope.launch {
            _uiState.update { CartUiState.Loading }
            val nextPage = currentPage + 1
            val result = cartRepository.getCartItems(nextPage, PAGE_SIZE)

            currentPage = nextPage
            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values,
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                )
            }
        }
    }

    fun goToPreviousPage() {
        val current = _uiState.value as? CartUiState.Success ?: return
        if (!current.hasPrevious) return

        viewModelScope.launch {
            _uiState.update { CartUiState.Loading }
            val prevPage = currentPage - 1
            val result = cartRepository.getCartItems(prevPage, PAGE_SIZE)

            currentPage = prevPage
            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values,
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        fun factory(cartRepository: CartRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(cartRepository)
                }
            }
    }
}
