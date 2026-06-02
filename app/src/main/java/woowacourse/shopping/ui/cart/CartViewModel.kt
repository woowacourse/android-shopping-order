package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.cart.uimodel.toUiModel
import woowacourse.shopping.ui.event.UiEvent

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private val _cartState = MutableStateFlow(CartState())
    val uiState: StateFlow<CartUiState> =
        _cartState
            .map { it.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CartUiState(),
            )

    init {
        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _cartState.update { it.copy(isLoading = true) }

            val cartPage = cartRepository.getCartPage(page = 0, size = PAGE_SIZE)
            _cartState.update {
                it.copy(
                    currentPage = 0,
                    pagedCart = cartPage.items,
                    hasNextPage = cartPage.isLast.not(),
                    allCartItems = cartPage.items,
                    isLoading = false,
                )
            }
        }
    }

    fun next() {
        viewModelScope.launch {
            val state = _cartState.value
            if (state.hasNextPage.not()) return@launch

            _cartState.update { it.copy(isLoading = true) }
            val nextPage = state.currentPage + 1
            loadPage(nextPage)
        }
    }

    fun prev() {
        viewModelScope.launch {
            val state = _cartState.value
            if (state.currentPage == 0) return@launch

            _cartState.update { it.copy(isLoading = true) }
            val prevPage = state.currentPage - 1
            loadPage(prevPage)
        }
    }

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = _cartState.value.pagedCart.findPurchaseProductById(id) ?: return@launch
            val nextCount = target.count + updateAmount
            if (nextCount < 1) return@launch

            _cartState.update { it.copy(isLoading = true) }
            try {
                cartRepository.updateCount(id, nextCount)

                _cartState.update {
                    it.copy(
                        pagedCart = it.pagedCart.updateCartItemCount(id, nextCount),
                        allCartItems = it.allCartItems.updateCartItemCount(id, nextCount),
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("수량 변경에 실패했습니다. 다시 시도해주세요."))
                _cartState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            _cartState.update { it.copy(isLoading = true) }
            try {
                cartRepository.deleteCartItem(id)

                val currentPage = _cartState.value.currentPage
                val page = cartRepository.getCartPage(currentPage, PAGE_SIZE)
                if (currentPage > 0 && page.items.purchaseProducts.isEmpty()) {
                    val previousPage = currentPage - 1
                    val previousCartPage = cartRepository.getCartPage(previousPage, PAGE_SIZE)
                    _cartState.update {
                        it.copy(
                            currentPage = previousPage,
                            pagedCart = previousCartPage.items,
                            hasNextPage = previousCartPage.isLast.not(),
                            allCartItems = it.allCartItems
                                .removeCartItem(id)
                                .mergeKnownCartItems(previousCartPage.items),
                            checkedItemIds = it.checkedItemIds - id,
                            isLoading = false,
                        )
                    }
                } else {
                    _cartState.update {
                        it.copy(
                            pagedCart = page.items,
                            hasNextPage = page.isLast.not(),
                            allCartItems = it.allCartItems
                                .removeCartItem(id)
                                .mergeKnownCartItems(page.items),
                            checkedItemIds = it.checkedItemIds - id,
                            isLoading = false,
                        )
                    }
                }

                _uiEvent.send(UiEvent.ShowMessage("상품을 삭제했습니다."))
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowMessage("아이템 삭제에 실패했습니다."))
                _cartState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onItemChecked(id: Long) {
        _cartState.update {
            val checkedItemIds =
                if (id in it.checkedItemIds) {
                    it.checkedItemIds - id
                } else {
                    it.checkedItemIds + id
                }
            it.copy(checkedItemIds = checkedItemIds)
        }
    }

    fun onSelectAllClick() {
        viewModelScope.launch {
            _cartState.update { it.copy(isLoading = true) }
            try {
                val allCartItems = cartRepository.getAllCartItems(PAGE_SIZE)
                val allIds = allCartItems.purchaseProducts.map { it.id }
                _cartState.update {
                    val checkedItemIds =
                        if (it.checkedItemIds.containsAll(allIds)) {
                            emptyList()
                        } else {
                            allIds
                        }
                    it.copy(
                        allCartItems = allCartItems,
                        checkedItemIds = checkedItemIds,
                        isLoading = false,
                    )
                }
            } finally {
                _cartState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun loadPage(page: Int) {
        val cartPage = cartRepository.getCartPage(page, PAGE_SIZE)
        _cartState.update {
            it.copy(
                currentPage = page,
                pagedCart = cartPage.items,
                hasNextPage = cartPage.isLast.not(),
                allCartItems = it.allCartItems.mergeKnownCartItems(cartPage.items),
                isLoading = false,
            )
        }
    }

    private fun PurchaseProducts.mergeKnownCartItems(cartItems: PurchaseProducts): PurchaseProducts {
        val newItems = cartItems.purchaseProducts
        return PurchaseProducts(
            purchaseProducts.filterNot { knownItem -> newItems.any { it.id == knownItem.id } } + newItems,
        )
    }

    private fun PurchaseProducts.updateCartItemCount(
        id: Long,
        count: Int,
    ): PurchaseProducts =
        PurchaseProducts(
            purchaseProducts.map {
                if (it.id == id) it.copy(count = count) else it
            },
        )

    private fun PurchaseProducts.removeCartItem(id: Long): PurchaseProducts =
        PurchaseProducts(purchaseProducts.filter { it.id != id })

    private fun CartState.toUiState(): CartUiState =
        CartUiState(
            cartItems = pagedCart.toUiModel(),
            currentPage = currentPage,
            isPageable = currentPage > 0 || hasNextPage,
            previousEnable = currentPage > 0,
            nextEnable = hasNextPage,
            isLoading = isLoading,
            totalPrice = totalPrice,
            totalCount = checkedItemIds.size,
            checkedItemIds = checkedItemIds,
        )

    private data class CartState(
        val pagedCart: PurchaseProducts = PurchaseProducts(),
        val currentPage: Int = 0,
        val hasNextPage: Boolean = false,
        val isLoading: Boolean = false,
        val allCartItems: PurchaseProducts = PurchaseProducts(),
        val checkedItemIds: List<Long> = emptyList(),
    ) {
        val totalPrice: Int
            get() =
                allCartItems.purchaseProducts
                    .filter { it.id in checkedItemIds }
                    .sumOf { it.totalPrice }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}

class CartViewModelFactory(
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
