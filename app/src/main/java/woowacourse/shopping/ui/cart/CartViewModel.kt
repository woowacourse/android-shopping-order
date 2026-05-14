package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.util.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems = MutableStateFlow<CartItems?>(null)
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    private val _isAllSelected = MutableStateFlow(false)

    private var currentPage = 0

    val uiState: StateFlow<CartUiState> =
        combine(
            _cartItems,
            _selectedItems,
            _isAllSelected,
        ) { cartItems, selectedItems, isAllSelected ->
            cartItems ?: return@combine CartUiState.Loading
            CartUiState.Success(
                cartItems = cartItems.values.toUiModel(selectedItems, isAllSelected),
                selectedItems = selectedItems,
                isAllSelected = isAllSelected,
                currentPage = currentPage,
                totalPages = cartItems.totalPages,
                hasPrevious = !cartItems.isFirst,
                hasNext = !cartItems.isLast,
                totalCount = cartItems.calculateQuantity(selectedItems, isAllSelected),
                totalPrice = cartItems.calculatePrice(selectedItems, isAllSelected),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading,
        )

    init {
        loadPage(0)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            _cartItems.update { null }
            val result = cartRepository.getCartItems(page, PAGE_SIZE)
            currentPage = page
            _cartItems.update { result }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            _cartItems.update { null }
            _selectedItems.update { it - cartId }
            cartRepository.remove(cartId)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)
            _cartItems.update { result }
        }
    }

    fun increase(cartId: Int) {
        viewModelScope.launch {
            val target = _cartItems.value?.values?.find { it.id == cartId } ?: return@launch

            cartRepository.increase(cartId, target.quantity.value + 1)
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _cartItems.update { result }
        }
    }

    fun decrease(cartId: Int) {
        viewModelScope.launch {
            val target = _cartItems.value?.values?.find { it.id == cartId } ?: return@launch

            if (target.quantity.value == 1) {
                cartRepository.remove(cartId)
                _selectedItems.update { it - cartId }
            } else {
                cartRepository.decrease(cartId, target.quantity.value - 1)
            }
            val result = cartRepository.getCartItems(currentPage, PAGE_SIZE)

            _cartItems.update { result }
        }
    }

    fun toggleSelection(id: Int) {
        _selectedItems.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        _isAllSelected.update { !it }
    }

    fun goToNextPage() {
        if (_cartItems.value?.isLast != false) return
        loadPage(currentPage + 1)
    }

    fun goToPreviousPage() {
        if (_cartItems.value?.isFirst != false) return
        loadPage(currentPage - 1)
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
