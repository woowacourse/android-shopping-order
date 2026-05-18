package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.recommender.ProductRecommender
import woowacourse.shopping.ui.common.model.ProductUiModel

class CartViewModel(
    private val recentProductRepo: RecentProductRepository,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository,
    private val pageSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState(pageSize = pageSize))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                refreshData()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun increase(item: CartItem) {
        viewModelScope.launch {
            val cartItemId = item.id
                ?: throw IllegalArgumentException("상품(${item.product.name})의 카트 아이템 아이디가 null 입니다.")
            cartRepo.updateQuantity(cartItemId, item.quantity + 1)
            _uiState.update { state ->
                val updatedItems = state.pagedItems.map {
                    if (it.id == cartItemId) it.copy(quantity = it.quantity + 1) else it
                }
                val isSelected = cartItemId in state.selectedItemIds
                state.copy(
                    pagedItems = updatedItems,
                    totalSelectedCount = if (isSelected) state.totalSelectedCount + 1 else state.totalSelectedCount,
                    totalSelectedPrice = if (isSelected) state.totalSelectedPrice + item.product.price.value else state.totalSelectedPrice
                )
            }
        }
    }

    fun decrease(item: CartItem) {
        viewModelScope.launch {
            val cartItemId = item.id
                ?: throw IllegalArgumentException("상품(${item.product.name})의 카트 아이템 아이디가 null 입니다.")
            if (item.quantity <= 1) {
                cartRepo.delete(cartItemId)
                refreshData()
            } else {
                cartRepo.updateQuantity(cartItemId, item.quantity - 1)
                _uiState.update { state ->
                    val updatedItems = state.pagedItems.map {
                        if (it.id == cartItemId) it.copy(quantity = it.quantity - 1) else it
                    }
                    val isSelected = cartItemId in state.selectedItemIds
                    state.copy(
                        pagedItems = updatedItems,
                        totalSelectedCount = if (isSelected) state.totalSelectedCount - 1 else state.totalSelectedCount,
                        totalSelectedPrice = if (isSelected) state.totalSelectedPrice - item.product.price.value else state.totalSelectedPrice
                    )
                }
            }
        }
    }

    fun delete(item: CartItem) {
        viewModelScope.launch {
            val cartItemId = item.id
                ?: throw IllegalArgumentException("상품(${item.product.name})의 카트 아이템 아이디가 null 입니다.")

            viewModelScope.launch {
                cartRepo.delete(cartItemId)

                val state = _uiState.value
                val wasSelected = cartItemId in state.selectedItemIds
                val newItems = state.pagedItems.filterNot { it.id == cartItemId }

                if (newItems.isEmpty() && state.currentPage > 0) {
                    _uiState.update { it.copy(currentPage = it.currentPage - 1) }
                    refreshData()
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        pagedItems = newItems,
                        selectedItemIds = state.selectedItemIds - cartItemId,
                        totalCartItemCount = it.totalCartItemCount - 1,
                        totalSelectedPrice = if (wasSelected) it.totalSelectedPrice - item.product.price.value else it.totalSelectedPrice,
                        totalSelectedCount = if (wasSelected) it.totalSelectedCount - 1 else it.totalSelectedCount
                    )
                }
            }
        }
    }

    fun nextPage() {
        val state = _uiState.value
        if (state.currentPage + 1 >= state.totalPages) return
        viewModelScope.launch {
            val page = cartRepo.getPagedItems(state.currentPage + 1, pageSize)
            _uiState.update {
                it.copy(currentPage = page.currentPage, pagedItems = page.items)
            }
        }
    }

    fun previousPage() {
        val state = _uiState.value
        if (state.currentPage <= 0) return
        viewModelScope.launch {
            val page = cartRepo.getPagedItems(state.currentPage - 1, pageSize)
            _uiState.update {
                it.copy(currentPage = page.currentPage, pagedItems = page.items)
            }
        }
    }

    fun toggleItemSelection(itemId: Long, isSelected: Boolean) {
        _uiState.update { state ->
            val newSelected = if (isSelected) state.selectedItemIds + itemId
            else state.selectedItemIds - itemId
            state.copy(
                selectedItemIds = newSelected,
                isAllSelected = newSelected.isNotEmpty() && newSelected.size == state.totalCartItemCount,
            )
        }
        viewModelScope.launch { recalculateTotals() }
    }

    fun toggleAllItemsSelection(isSelected: Boolean) {
        viewModelScope.launch {
            val cartItems = cartRepo.getAllCartItems()
            val allIds = cartItems.items
                .map { it.id ?: throw IllegalArgumentException("아이템에 id가 없습니다.") }
                .toSet()

            _uiState.update {
                if (isSelected && cartItems.items.isNotEmpty()) {
                    it.copy(selectedItemIds = allIds, isAllSelected = true)
                } else {
                    it.copy(selectedItemIds = emptySet(), isAllSelected = false)
                }
            }
            recalculateTotals()
        }
    }

    fun changeScreen() {
        val state = _uiState.value
        if (state.isCartScreen && state.selectedItemIds.isNotEmpty()) {
            _uiState.update { it.copy(recommendItems = emptyList(), isCartScreen = false) }
            getRecommendProducts()
        } else {
            _uiState.update { it.copy(isCartScreen = true) }
        }
    }

    fun order(selectedIds: List<Long>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                orderRepo.requestOrder(selectedIds)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun increaseInRecommendScreen(uiModel: ProductUiModel) {
        viewModelScope.launch {
            val cartItemId = if (uiModel.cartItemId == null) {
                cartRepo.add(uiModel.product.id, quantity = 1)
            } else {
                cartRepo.updateQuantity(uiModel.cartItemId, uiModel.quantity + 1)
                uiModel.cartItemId
            }

            _uiState.update { state ->
                val newUiModels = state.recommendItems.map {
                    if (it.product.id == uiModel.product.id) {
                        it.copy(
                            quantity = uiModel.quantity + 1,
                            cartItemId = cartItemId,
                        )
                    } else it
                }
                state.copy(
                    recommendItems = newUiModels,
                    selectedItemIds = state.selectedItemIds + cartItemId,
                    totalSelectedCount = state.totalSelectedCount + 1,
                    totalSelectedPrice = state.totalSelectedPrice + uiModel.product.price.value
                )
            }
        }
    }

    fun decreaseInRecommendScreen(uiModel: ProductUiModel) {
        viewModelScope.launch {
            val cartItemId = uiModel.cartItemId
                ?: throw IllegalArgumentException("상품(${uiModel.product.name})에 카트아이템 아이디가 없습니다.")

            if (uiModel.quantity > 1) {
                cartRepo.updateQuantity(cartItemId, uiModel.quantity - 1)
            } else {
                cartRepo.delete(cartItemId)
            }

            _uiState.update { state ->
                val newUiModels = state.recommendItems.map {
                    if (it.product.id == uiModel.product.id) {
                        it.copy(
                            quantity = maxOf(0, uiModel.quantity - 1),
                            cartItemId = if (uiModel.quantity == 1) null else cartItemId
                        )
                    } else it
                }
                state.copy(
                    recommendItems = newUiModels,
                    selectedItemIds = if (uiModel.quantity == 1) state.selectedItemIds - cartItemId
                    else state.selectedItemIds,
                    totalSelectedPrice = state.totalSelectedPrice - uiModel.product.price.value,
                    totalSelectedCount = state.totalSelectedCount - 1
                )
            }
        }
    }

    private fun getRecommendProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val products = ProductRecommender.getRecommendProducts(
                    lastViewedItem = recentProductRepo.getLastViewedProduct(),
                    allProductItems = productRepo.getProducts(0, 50).items,
                    allCartItem = cartRepo.getAllCartItems().items,
                )
                val uiModels = products.map { ProductUiModel(product = it) }
                _uiState.update { it.copy(recommendItems = uiModels) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun refreshData() {
        val requestedPage = _uiState.value.currentPage.coerceAtLeast(0)
        val initialPage = cartRepo.getPagedItems(requestedPage, pageSize)

        val finalPage = if (initialPage.totalPages in 1..requestedPage) {
            cartRepo.getPagedItems(initialPage.totalPages - 1, pageSize)
        } else {
            initialPage
        }

        val totalPrice = calculatePrice()
        val totalSelectedCount = calculateTotalSelectedCount()

        _uiState.update {
            it.copy(
                currentPage = finalPage.currentPage,
                pagedItems = finalPage.items,
                totalCartItemCount = finalPage.totalElements,
                totalPages = finalPage.totalPages,
                totalSelectedPrice = totalPrice,
                totalSelectedCount = totalSelectedCount,
                isAllSelected = it.selectedItemIds.isNotEmpty()
                        && it.selectedItemIds.size == finalPage.totalElements,
            )
        }
    }

    private suspend fun recalculateTotals() {
        val totalPrice = calculatePrice()
        val totalSelectedCount = calculateTotalSelectedCount()
        _uiState.update {
            it.copy(totalSelectedPrice = totalPrice, totalSelectedCount = totalSelectedCount)
        }
    }

    private suspend fun calculatePrice(): Long {
        val selectedIds = _uiState.value.selectedItemIds
        if (selectedIds.isEmpty()) return 0
        val cart = cartRepo.getAllCartItems()
        return selectedIds.sumOf { itemId ->
            cart.items.find { it.id == itemId }?.totalPrice?.value ?: 0
        }
    }

    private suspend fun calculateTotalSelectedCount(): Int {
        val selectedIds = _uiState.value.selectedItemIds
        if (selectedIds.isEmpty()) return 0
        val cart = cartRepo.getAllCartItems()
        return selectedIds.sumOf { itemId ->
            cart.items.find { it.id == itemId }?.quantity ?: 0
        }
    }

    companion object {
        fun provideFactory(
            cartRepo: CartRepository,
            productRepo: ProductRepository,
            orderRepo: OrderRepository,
            recentProductRepo: RecentProductRepository,
            pageSize: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CartViewModel(
                        cartRepo = cartRepo,
                        pageSize = pageSize,
                        recentProductRepo = recentProductRepo,
                        productRepo = productRepo,
                        orderRepo = orderRepo,
                    ) as T
            }
    }
}