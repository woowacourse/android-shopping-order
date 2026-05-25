package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val cartItems: StateFlow<CartItems> = cartRepository.cartItems
    private val selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedCartIds: StateFlow<Set<Int>> = selectedItems.asStateFlow()
    private val currentPageIndex = MutableStateFlow(0)
    private val cartLoadState = MutableStateFlow<LoadState>(LoadState.Initial)

    val cartUiState: StateFlow<CartUiState> =
        combine(
            cartItems,
            selectedItems,
            currentPageIndex,
            cartLoadState,
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
            cartLoadState.update { LoadState.Loading }
            runCatching {
                cartRepository.refreshCartItems()
                cartLoadState.update { LoadState.Success }
            }.onFailure { throwable ->
                cartLoadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    private fun syncSelectedItems() {
        viewModelScope.launch {
            cartItems.collect { cartItems ->
                val currentCartIds = cartItems.values.map { it.id }.toSet()
                selectedItems.update { it.intersect(currentCartIds) }
            }
        }
    }

    fun removeCartItem(cartId: Int) {
        viewModelScope.launch {
            runCatching {
                cartRepository.removeCartItem(cartId)
                val totalPage = (cartItems.value.size() + PAGE_SIZE - 1) / PAGE_SIZE
                if (currentPageIndex.value >= totalPage) {
                    currentPageIndex.update { maxOf(0, totalPage - 1) }
                }
            }.onFailure { throwable ->
                cartLoadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun addCartItem(productId: Int) {
        viewModelScope.launch {
            runCatching {
                cartRepository.addProduct(productId, 1)
                val cartItem = cartItems.value.findByProductId(productId) ?: return@launch

                selectedItems.update { it + cartItem.id }
            }.onFailure { throwable ->
                cartLoadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun increaseCartItemQuantity(cartId: Int) {
        viewModelScope.launch {
            runCatching {
                val targetQuantity = cartItems.value.getQuantityByCartId(cartId)
                cartRepository.updateQuantity(cartId, targetQuantity.value + 1)
            }.onFailure { throwable ->
                cartLoadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun decreaseCartItemQuantity(cartId: Int) {
        viewModelScope.launch {
            runCatching {
                val targetQuantity = cartItems.value.getQuantityByCartId(cartId)

                if (targetQuantity.value == 1) {
                    cartRepository.removeCartItem(cartId)
                    selectedItems.update { it - cartId }
                } else {
                    cartRepository.updateQuantity(cartId, targetQuantity.value - 1)
                }
            }.onFailure { throwable ->
                cartLoadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun toggleSelection(id: Int) {
        selectedItems.update { current ->
            if (id in current) current - id else current + id
        }
    }

    fun toggleAllSelection() {
        val current = selectedItems.value
        if (current.size == cartItems.value.values.size) {
            selectedItems.update { emptySet() }
        } else {
            selectedItems.update {
                cartItems.value.values
                    .map { it.id }
                    .toSet()
            }
        }
    }

    fun goToNextPage() {
        if (!cartUiState.value.hasNextPage) return
        currentPageIndex.update { it + 1 }
    }

    fun goToPreviousPage() {
        if (!cartUiState.value.hasPreviousPage) return
        currentPageIndex.update { it - 1 }
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
