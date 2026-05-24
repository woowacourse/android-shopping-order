package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.event.UiEvent

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val _currentPage: MutableStateFlow<Int> = MutableStateFlow(0)

    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _pagedCart: MutableStateFlow<PurchaseProducts> = MutableStateFlow(PurchaseProducts())
    val pagedCart: StateFlow<PurchaseProducts> = _pagedCart.asStateFlow()

    private val _hasNextPage = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _allCartItems = MutableStateFlow<PurchaseProducts>(PurchaseProducts())
    val allCartItems = _allCartItems.asStateFlow()

    private val _checkedItemIds = MutableStateFlow<List<Long>>(emptyList())
    val checkedItemIds = _checkedItemIds.asStateFlow()

    val totalPrice: StateFlow<Int> = combine(_allCartItems, checkedItemIds) { allCart, checkedIds ->
        allCart.purchaseProducts
            .filter { it.id in checkedIds }
            .sumOf { it.totalPrice }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val selectedItemCount: StateFlow<Int> = checkedItemIds.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.update { true }

            _currentPage.update { 0 }
            loadPage(0, resetKnownCartItems = true)

            _isLoading.update { false }
        }
    }

    init {
        fetchCart()
    }

    fun next() {
        viewModelScope.launch {
            if (_hasNextPage.value.not()) return@launch

            _isLoading.update { true }
            val nextPage = currentPage.value + 1
            _currentPage.update { nextPage }
            loadPage(nextPage)
            _isLoading.update { false }
        }
    }

    fun prev() {
        viewModelScope.launch {
            if (currentPage.value == 0) return@launch

            _isLoading.update { true }
            val prevPage = currentPage.value - 1
            _currentPage.update { prevPage }
            loadPage(prevPage)
            _isLoading.update { false }
        }
    }

    val nextEnable: StateFlow<Boolean> = _hasNextPage.asStateFlow()

    val prevEnable: StateFlow<Boolean> =
        currentPage
            .map { it > 0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    val isPageable: StateFlow<Boolean> =
        combine(currentPage, _hasNextPage) { page, hasNextPage ->
            page > 0 || hasNextPage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            val target = pagedCart.value.findPurchaseProductById(id) ?: return@launch
            val nextCount = target.count + updateAmount
            if (nextCount < 1) return@launch

            _isLoading.update { true }
            try {
                cartRepository.updateCount(id, nextCount)
                
                updateKnownCartItemCount(id, nextCount)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("수량 변경에 실패했습니다. 다시 시도해주세요."))
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                cartRepository.deleteCartItem(id)

                removeKnownCartItem(id)

                val page = cartRepository.getCartPage(currentPage.value, PAGE_SIZE)
                if (currentPage.value > 0 && page.items.purchaseProducts.isEmpty()) {
                    val previousPage = currentPage.value - 1
                    _currentPage.update { previousPage }
                    loadPage(previousPage)
                } else {
                    _pagedCart.update { page.items }
                    _hasNextPage.update { page.isLast.not() }
                    mergeKnownCartItems(page.items)
                }

                _checkedItemIds.update { it - id }
                _uiEvent.emit(UiEvent.ShowMessage("상품을 삭제했습니다."))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("아이템 삭제에 실패했습니다."))
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun onItemChecked(id: Long) {
        viewModelScope.launch {
            _checkedItemIds.update {
                if (it.contains(id)) {
                    it - id
                } else {
                    it + id
                }
            }
        }
    }

    fun onSelectAllClick() {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val allCartItems = cartRepository.getAllCartItems(PAGE_SIZE)
                _allCartItems.update { allCartItems }
                val allIds = allCartItems.purchaseProducts.map { it.id }
                _checkedItemIds.update { list ->
                    if (list.containsAll(allIds)) {
                        emptyList()
                    } else {
                        allIds
                    }
                }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    private suspend fun loadPage(
        page: Int,
        resetKnownCartItems: Boolean = false,
    ) {
        val cartPage = cartRepository.getCartPage(page, PAGE_SIZE)
        _pagedCart.update { cartPage.items }
        _hasNextPage.update { cartPage.isLast.not() }
        if (resetKnownCartItems) {
            _allCartItems.update { cartPage.items }
        } else {
            mergeKnownCartItems(cartPage.items)
        }
    }

    private fun mergeKnownCartItems(cartItems: PurchaseProducts) {
        _allCartItems.update { knownItems ->
            val newItems = cartItems.purchaseProducts
            PurchaseProducts(
                knownItems.purchaseProducts
                    .filterNot { knownItem -> newItems.any { it.id == knownItem.id } } + newItems,
            )
        }
    }

    private fun updateKnownCartItemCount(
        id: Long,
        count: Int,
    ) {
        _pagedCart.update { it.updateCartItemCount(id, count) }
        _allCartItems.update { it.updateCartItemCount(id, count) }
    }

    private fun removeKnownCartItem(id: Long) {
        _pagedCart.update { it.removeCartItem(id) }
        _allCartItems.update { it.removeCartItem(id) }
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

    companion object {
        private const val PAGE_SIZE = 5
    }
}

class CartViewModelFactory(
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
