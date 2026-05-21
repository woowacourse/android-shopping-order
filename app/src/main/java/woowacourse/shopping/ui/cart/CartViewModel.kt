package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.recommender.ProductRecommender
import woowacourse.shopping.ui.common.model.ProductUiModel
import woowacourse.shopping.ui.common.paging.Pager

class CartViewModel(
    private val recentProductRepo: RecentProductRepository,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository,
    private val pageSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()
    val pager = Pager(pageSize)

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

    fun increase(product: Product) {
        viewModelScope.launch {
            try {
                cartRepo.add(product)
                loadData()
            } finally {
            }
        }
    }

    fun decrease(product: Product) {
        viewModelScope.launch {
            try {
                cartRepo.decrease(product)
                loadData()
            } finally {
            }
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            try {
                val cartItem =
                    cartRepo.getAllCartItems().items.find { it.product.id == product.id }
                        ?: throw IllegalArgumentException("추가하려는 상품 아이디(${product.id})로 장바구니 아이디를 조회할 수 없습니다. ")
                val cartId =
                    cartItem.id
                        ?: throw IllegalArgumentException("카트 아이템 아이디가 생성되지 않았습니다.")

                _uiState.update { it.copy(selectedItemIds = it.selectedItemIds - cartId) }
                cartRepo.delete(product)

                loadData()
            } finally {
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

    fun toggleItemSelection(
        itemId: Long,
        isSelected: Boolean,
    ) {
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
            try {
                val cartItems = cartRepo.getAllCartItems()
                val allIds =
                    cartItems.items
                        .mapNotNull { it.id }
                        .toSet()

                if (isSelected && cartItems.items.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            selectedItemIds = allIds,
                            isAllSelected = true,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            selectedItemIds = emptySet(),
                            isAllSelected = false,
                        )
                    }
                }
                refreshData()
            } finally {
            }
        }
    }

    fun changeScreen() {
        if (_uiState.value.isCartScreen && _uiState.value.selectedItemIds.isNotEmpty()) {
            getRecommendProducts()
            _uiState.update { it.copy(isCartScreen = false) }
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

    fun increaseInRecommendScreen(product: Product) {
        viewModelScope.launch {
            try {
                cartRepo.add(product)
                val cartId = cartRepo.findCartItem(product.id)
                        ?.id
                        ?: throw IllegalArgumentException("추가하려는 상품 아이디(${product.id})로 장바구니 아이디를 조회할 수 없습니다. ")

                _uiState.update { state ->
                    val uiModel =
                        state.recommendItems.map { uiModel ->
                            if (uiModel.product.id == product.id) {
                                uiModel.copy(cartQuantity = uiModel.cartQuantity + 1)
                            } else {
                                uiModel
                            }
                        }

                    state.copy(
                        recommendItems = uiModel,
                        selectedItemIds = state.selectedItemIds + cartId,
                    )
                }
                refreshData()
            } finally {
            }
        }
    }

    fun decreaseInRecommendScreen(product: Product) {
        viewModelScope.launch {
            try {
                val cartItem =
                    cartRepo.findCartItem(product.id)
                        ?: throw IllegalArgumentException("추가하려는 상품 아이디(${product.id})로 장바구니 아이디를 조회할 수 없습니다. ")
                val cartId =
                    cartItem.id
                        ?: throw IllegalArgumentException("카트 아이템 아이디가 생성되지 않았습니다.")

                if (cartItem.quantity > 1) cartRepo.decrease(product) else cartRepo.delete(product)

                _uiState.update { state ->
                    val uiModel =
                        state.recommendItems.map { uiModel ->
                            if (uiModel.product.id == product.id) {
                                uiModel.copy(cartQuantity = maxOf(0, uiModel.cartQuantity - 1))
                            } else {
                                uiModel
                            }
                        }
                    state.copy(
                        recommendItems = uiModel,
                        selectedItemIds = if (cartItem.quantity == 1) state.selectedItemIds - cartId else state.selectedItemIds,
                    )
                }
                refreshData()
            } finally {
            }
        }
    }

    private fun getRecommendProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val products =
                    ProductRecommender.recommendProduct(
                        lastViewedItem = recentProductRepo.getLastViewedProduct(),
                        allProductItems = productRepo.getProducts(0, 50),
                        allCartItem = cartRepo.getAllCartItems().items,
                        MAX_RECOMMEND_ITEM_SIZE
                    )
                val uiModel =
                    products.map {
                        ProductUiModel(product = it)
                    }
                _uiState.update { it.copy(recommendItems = uiModel) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                refreshData()
            } finally {
            }
        }
    }

    private suspend fun refreshData() {
        val totalCartItemCount = cartRepo.getSize()
        val totalPages = pager.getTotalPages(totalCartItemCount)
        val validCurrentPage = _uiState.value.currentPage.coerceIn(1, totalPages)
        val totalPrice = calculatePrice()
        val totalSelectedCount = calculateTotalSelectedCount()
        val items =
            cartRepo.getPagedItems(
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
                totalSelectedCount = totalSelectedCount,
            )
        }

        if (_uiState.value.selectedItemIds.isNotEmpty() && _uiState.value.selectedItemIds.size == _uiState.value.totalCartItemCount) {
            _uiState.update { it.copy(isAllSelected = true) }
        }
    }

    private suspend fun calculatePrice(): Long {
        val cart = cartRepo.getAllCartItems()
        if (cart.items.isEmpty()) return 0
        val selectedItems =
            _uiState.value.selectedItemIds.mapNotNull { itemId ->
                cart.items.find { it.id == itemId }
            }
        return selectedItems.sumOf { it.totalPrice.value }
    }

    private suspend fun calculateTotalSelectedCount(): Int {
        val cart = cartRepo.getAllCartItems()
        if (cart.items.isEmpty()) return 0
        val selectedItems =
            _uiState.value.selectedItemIds.mapNotNull { itemId ->
                cart.items.find { it.id == itemId }
            }
        return selectedItems.sumOf { it.quantity }
    }

    companion object {
        const val MAX_RECOMMEND_ITEM_SIZE = 20

        fun provideFactory(
            container: AppContainer,
            pageSize: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CartViewModel(
                        recentProductRepo = container.recentProductRepository,
                        productRepo = container.productRepository,
                        cartRepo = container.cartRepository,
                        orderRepo = container.orderRepository,
                        pageSize = pageSize,
                    ) as T
            }
    }
}
