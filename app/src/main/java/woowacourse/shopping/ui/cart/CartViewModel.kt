package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.ui.common.paging.Pager

class CartViewModel(
    private val cartRepo: CartRepository,
    private val pageSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()
    val pager = Pager(pageSize)

    init {
        loadData()
    }

    fun increase(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                cartRepo.increase(product)
                loadData()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun decrease(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                cartRepo.decrease(product)
                loadData()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                cartRepo.delete(product)
                loadData()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun nextPage() {
        val currentPage = _uiState.value.currentPage
        val totalCount = _uiState.value.totalCartItemCount
        if (pager.hasNext(currentPage, totalCount)) {
            _uiState.update { it.copy(currentPage = currentPage + 1) }
            loadData()
        }
    }

    fun previousPage() {
        val currentPage = _uiState.value.currentPage
        if (pager.hasPrevious(currentPage)) {
            _uiState.update { it.copy(currentPage = currentPage - 1) }
            loadData()
        }
    }

    fun toggleItemSelection(itemId: Long, isSelected: Boolean) {
        _uiState.update { state ->
            if (isSelected) {
                state.copy(selectedItemIds = state.selectedItemIds + itemId)
            } else {
                state.copy(selectedItemIds = state.selectedItemIds - itemId, isAllSelected = false)
            }
        }
        loadData()
    }

    fun toggleAllItemsSelection(isSelected: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val cartItems = cartRepo.getAllCartItems()
                val allIds = cartItems.items.map {
                    it.id
                        ?: throw IllegalArgumentException("아이템에 id(${it.id})가 없습니다.")
                }.toSet()

                if (isSelected) _uiState.update {
                    it.copy(
                        selectedItemIds = allIds,
                        isAllSelected = true
                    )
                }
                else _uiState.update {
                    it.copy(
                        selectedItemIds = emptySet(),
                        isAllSelected = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                refreshData()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun refreshData() {
        val totalCartItemCount = cartRepo.getSize()
        val totalPages = pager.getTotalPages(totalCartItemCount)
        val validCurrentPage = _uiState.value.currentPage.coerceIn(1, totalPages)
        val totalPrice = calculatePrice()
        val totalSelectedCount = calculateTotalSelectedCount()
        val items = cartRepo.getPagedItems(
            page = validCurrentPage,
            count = pageSize,
        )

        _uiState.update {
            it.copy(
                currentPage = validCurrentPage,
                pagedItems = items,
                totalCartItemCount = totalCartItemCount,
                pageSize = pageSize,
                totalPrice = totalPrice,
                totalSelectedCount = totalSelectedCount
            )
        }
    }

    private suspend fun calculatePrice(): Long {
        val cart = cartRepo.getAllCartItems()
        if (cart.items.isEmpty()) return 0
        val selectedItems = _uiState.value.selectedItemIds.map { itemId ->
            cart.items.find { it.id == itemId }
                ?: throw IllegalArgumentException("선택한 상품 아이디($itemId)로 장바구니에서 아이템을 조회할 수 없습니다.")
        }
        return selectedItems.sumOf { it.totalPrice.value }
    }

    private suspend fun calculateTotalSelectedCount(): Int {
        val cart = cartRepo.getAllCartItems()
        if (cart.items.isEmpty()) return 0
        val selectedItems = _uiState.value.selectedItemIds.map { itemId ->
            cart.items.find { it.id == itemId }
                ?: throw IllegalArgumentException("선택한 상품 아이디($itemId)로 장바구니에서 아이템을 조회할 수 없습니다.")
        }
        return selectedItems.sumOf { it.quantity }
    }
}
