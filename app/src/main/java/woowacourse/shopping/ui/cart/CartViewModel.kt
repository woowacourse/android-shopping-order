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
import woowacourse.shopping.ui.util.toUiModel

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
                    cartItems = result.values.toUiModel(emptySet()),
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
            val currentState = _uiState.value as? CartUiState.Success ?: return@launch

            _uiState.update { CartUiState.Loading }
            cartRepository.remove(cartId)

            val newSelected = currentState.selectedItems - cartId

            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values.toUiModel(newSelected),
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                    selectedItems = newSelected,
                )
            }
        }
    }

    fun increase(cartId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value as? CartUiState.Success ?: return@launch

            val target = currentState.cartItems.find { it.id == cartId } ?: return@launch
            cartRepository.increase(cartId, target.quantity + 1)

            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values.toUiModel(currentState.selectedItems),
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                    selectedItems = currentState.selectedItems,
                )
            }
        }
    }

    fun decrease(cartId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value as? CartUiState.Success ?: return@launch

            val target = currentState.cartItems.find { it.id == cartId } ?: return@launch
            cartRepository.decrease(cartId, target.quantity - 1)

            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            val newSelected =
                if (target.quantity - 1 == 0 && currentState.selectedItems.contains(cartId)) {
                    currentState.selectedItems - cartId
                } else {
                    currentState.selectedItems
                }

            _uiState.update {
                CartUiState.Success(
                    cartItems = result.values.toUiModel(newSelected),
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                    selectedItems = newSelected,
                )
            }
        }
    }

    fun toggleSelection(id: Int) {
        _uiState.update { state ->
            val success = state as? CartUiState.Success ?: return@update state
            val newSelected =
                if (id in success.selectedItems) {
                    success.selectedItems - id
                } else {
                    success.selectedItems + id
                }
            success.copy(
                selectedItems = newSelected,
                cartItems =
                    success.cartItems.map {
                        it.copy(isSelected = it.id in newSelected)
                    },
            )
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
                    cartItems = result.values.toUiModel(current.selectedItems),
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                    selectedItems = current.selectedItems,
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
                    cartItems = result.values.toUiModel(current.selectedItems),
                    currentPage = currentPage,
                    totalPages = result.totalPages,
                    hasPrevious = !result.isFirst,
                    hasNext = !result.isLast,
                    selectedItems = current.selectedItems,
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
