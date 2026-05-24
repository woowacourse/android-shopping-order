package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.util.LoadState
import woowacourse.shopping.ui.util.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val cartItemsFlow: StateFlow<CartItems> = cartRepository.cartItems
    private val selectedItemsFlow = MutableStateFlow<Set<Int>>(emptySet())
    private val currentPageIdxFlow = MutableStateFlow(0)
    private val cartLoadStateFlow = MutableStateFlow<LoadState>(LoadState.Initial)

    private val _events = MutableSharedFlow<CartEvent>()
    val events: SharedFlow<CartEvent> = _events.asSharedFlow()

    val cartUiState: StateFlow<CartUiState> =
        combine(
            cartItemsFlow,
            selectedItemsFlow,
            currentPageIdxFlow,
            cartLoadStateFlow,
        ) { cartItems, selectedItems, currentPageIdx, loadState ->
            val curCartItems =
                cartItems.subList(currentPageIdx * PAGE_SIZE, (currentPageIdx + 1) * PAGE_SIZE)
            CartUiState(
                cartItems = curCartItems.values.toUiModel(selectedItems),
                currentPage = currentPageIdx + 1,
                hasPreviousPage = !curCartItems.isFirst,
                hasNextPage = !curCartItems.isLast,
                isAllSelected = selectedItems.containsAll(cartItems.values.map { it.id }),
                totalPrice = cartItems.calculatePrice(selectedItems),
                totalCount = cartItems.calculateQuantity(selectedItems),
                showPageNavigator = cartItems.size() > PAGE_SIZE,
                loadState = loadState,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState(),
        )

    init {
        initCartItems()
        syncSelectedItems()
    }

    private fun initCartItems() {
        viewModelScope.launch {
            cartLoadStateFlow.update { LoadState.Loading }
            runCatching {
                cartRepository.refreshCartItems()
                cartLoadStateFlow.update { LoadState.Success }
            }.onFailure { throwable ->
                cartLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    private fun syncSelectedItems() {
        viewModelScope.launch {
            cartItemsFlow.collect { cartItems ->
                val currentCartIds = cartItems.values.map { it.id }.toSet()
                selectedItemsFlow.update { it.intersect(currentCartIds) }
            }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            runCatching {
                cartRepository.removeCartItem(cartId)
                val totalPage = (cartItemsFlow.value.size() + PAGE_SIZE - 1) / PAGE_SIZE
                if (currentPageIdxFlow.value >= totalPage) {
                    currentPageIdxFlow.update { maxOf(0, totalPage - 1) }
                }
            }.onFailure { throwable ->
                cartLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun addCartItem(productId: Int) {
        viewModelScope.launch {
            runCatching {
                cartRepository.addProduct(productId, 1)
                val cartItem = cartItemsFlow.value.findByProductId(productId) ?: return@launch

                selectedItemsFlow.update { it + cartItem.id }
            }.onFailure { throwable ->
                cartLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun increaseCartItemQuantity(cartId: Int) {
        viewModelScope.launch {
            runCatching {
                val targetQuantity = cartItemsFlow.value.getQuantityByCartId(cartId)
                cartRepository.updateQuantity(cartId, targetQuantity.value + 1)
            }.onFailure { throwable ->
                cartLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun decreaseCartItemQuantity(cartId: Int) {
        viewModelScope.launch {
            runCatching {
                val targetQuantity = cartItemsFlow.value.getQuantityByCartId(cartId)

                if (targetQuantity.value == 1) {
                    cartRepository.removeCartItem(cartId)
                    selectedItemsFlow.update { it - cartId }
                } else {
                    cartRepository.updateQuantity(cartId, targetQuantity.value - 1)
                }
            }.onFailure { throwable ->
                cartLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun toggleSelection(id: Int) {
        selectedItemsFlow.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        val current = selectedItemsFlow.value
        if (current.size == cartItemsFlow.value.values.size) {
            selectedItemsFlow.update { emptySet() }
        } else {
            selectedItemsFlow.update {
                cartItemsFlow.value.values
                    .map { it.id }
                    .toSet()
            }
        }
    }

    fun goToNextPage() {
        if (!cartUiState.value.hasNextPage) return
        currentPageIdxFlow.update { it + 1 }
    }

    fun goToPreviousPage() {
        if (!cartUiState.value.hasPreviousPage) return
        currentPageIdxFlow.update { it - 1 }
    }

    fun onClickOrder() {
        viewModelScope.launch {
            runCatching {
                cartRepository.order(selectedItemsFlow.value.toList())
                _events.emit(CartEvent.OrderSuccess)
            }.onFailure { throwable ->
                cartLoadStateFlow.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                    CartViewModel(
                        application.appContainer.cartRepository,
                    )
                }
            }
    }
}

sealed interface CartEvent {
    data object OrderSuccess : CartEvent
}
