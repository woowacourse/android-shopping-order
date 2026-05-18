package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.domain.PurchaseProducts

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _currentPage: MutableStateFlow<Int> = MutableStateFlow(0)

    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _pagedCart: MutableStateFlow<PurchaseProducts> = MutableStateFlow(PurchaseProducts())
    val pagedCart: StateFlow<PurchaseProducts> = _pagedCart.asStateFlow()

    private val _cartItemCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val cartItemCount = _cartItemCount.asStateFlow()

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

    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.update { true }

            val firstPage = cartRepository.getPagedCart(0, PAGE_SIZE)
            _pagedCart.update { firstPage }
            
            val totalCount = cartRepository.getCartItemCount()
            _cartItemCount.update { totalCount }
            
            _isLoading.update { false }

            if (totalCount > 0) {
                launch(Dispatchers.IO) {
                    val allData = cartRepository.getPagedCart(0, totalCount)
                    _allCartItems.update { allData }
                }
            }
        }
    }

    init {
        fetchCart()
    }

    fun next() {
        _currentPage.update {
            if (cartItemCount.value > (it + 1) * PAGE_SIZE) it + 1 else it
        }
        viewModelScope.launch {
            _isLoading.update { true }
            _pagedCart.update {
                cartRepository.getPagedCart(currentPage.value, PAGE_SIZE)
            }
            _isLoading.update { false }
        }
    }

    fun prev() {
        _currentPage.update { if (it > 0) it - 1 else 0 }
        viewModelScope.launch {
            _isLoading.update { true }
            _pagedCart.update {
                cartRepository.getPagedCart(currentPage.value, PAGE_SIZE)
            }
            _isLoading.update { false }
        }
    }

    val nextEnable: StateFlow<Boolean> =
        combine(currentPage, cartItemCount) { page, count ->
            page < (count - 1) / PAGE_SIZE
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val prevEnable: StateFlow<Boolean> =
        currentPage
            .map { it > 0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    val isPageable: StateFlow<Boolean> =
        cartItemCount
            .map { it > PAGE_SIZE }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    fun updateCountWithID(
        id: Long,
        updateAmount: Int,
    ) {
        viewModelScope.launch {
            _isLoading.update { true }
            val target = pagedCart.value.findPurchaseProductById(id)
            if (target != null) {
                val nextCount = target.count + updateAmount
                if (nextCount >= 1) {
                    cartRepository.updateCount(id, nextCount)
                    updateAllCartItemCount(id, nextCount)
                    _pagedCart.update {
                        cartRepository.getPagedCart(currentPage.value, PAGE_SIZE)
                    }
                    _cartItemCount.update {
                        cartRepository.getCartItemCount()
                    }
                }
            }
            _isLoading.update { false }
        }
    }

    fun removeWithID(id: Long) {
        viewModelScope.launch {
            _isLoading.update { true }
            cartRepository.deleteCartItem(id)

            val newTotalCount = cartRepository.getCartItemCount()
            _cartItemCount.update {
                newTotalCount
            }

            if(currentPage.value > 0 && currentPage.value * PAGE_SIZE >= newTotalCount){
                _currentPage.update { it - 1 }
            }

            _pagedCart.update {
                cartRepository.getPagedCart(currentPage.value, PAGE_SIZE)
            }

            _isLoading.update { false }
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
            _checkedItemIds.update { list ->
                val allIds = _allCartItems.value.purchaseProducts.map { it.id }
                if (list.containsAll(allIds)) {
                    emptyList()
                } else {
                    allIds
                }
            }
        }
    }

    private fun updateAllCartItemCount(
        id: Long,
        newCount: Int,
    ) {
        _allCartItems.update { allCart ->
            PurchaseProducts(
                allCart.purchaseProducts.map {
                    if (it.id == id) it.copy(count = newCount) else it
                },
            )
        }
    }

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
