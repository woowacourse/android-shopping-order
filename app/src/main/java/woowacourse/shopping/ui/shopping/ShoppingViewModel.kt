package woowacourse.shopping.ui.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.NetworkMonitor
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.common.model.ProductUiModel
import woowacourse.shopping.ui.common.paging.Pager

class ShoppingViewModel(
    networkMonitor: NetworkMonitor,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val recentProductRepo: RecentProductRepository,
    private val loadSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    private val pager = Pager(loadSize)
    val uiState = _uiState.asStateFlow()
    val isNetworkConnected: StateFlow<Boolean> =
        networkMonitor.isConnected
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = true,
            )

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val page = productRepo.getProducts(0, loadSize)
                val recentProducts = recentProductRepo.getRecentProducts()

                _uiState.update {
                    it.copy(
                        visibleProducts = mapToProductUiModels(page.items),
                        hasNext = !page.isLast,
                        sizeInRepo = page.totalElements,
                        recentProducts = recentProducts,
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun increase(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                cartRepo.increase(product)

                _uiState.update { state ->
                    val updatedProducts =
                        state.visibleProducts.map { uiModel ->
                            if (uiModel.product.id == product.id) {
                                uiModel.copy(cartQuantity = uiModel.cartQuantity + 1)
                            } else {
                                uiModel
                            }
                        }
                    val cartCount = state.cartCount + 1
                    state.copy(visibleProducts = updatedProducts, cartCount = cartCount)
                }
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

                _uiState.update { state ->
                    val updatedProducts =
                        state.visibleProducts.map { uiModel ->
                            if (uiModel.product.id == product.id) {
                                uiModel.copy(cartQuantity = maxOf(0, uiModel.cartQuantity - 1))
                            } else {
                                uiModel
                            }
                        }
                    val cartCount = maxOf(0, state.cartCount - 1)
                    state.copy(visibleProducts = updatedProducts, cartCount = cartCount)
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMore() {
        val currentSize = _uiState.value.visibleProducts.size
        val currentProducts = _uiState.value.visibleProducts
        if (!pager.canLoadMore(currentSize, _uiState.value.sizeInRepo)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val page = productRepo.getProducts(
                    // 마지막 페이지가 부분(예: 25개 중 5개)일 때도 다음 페이지 정확히 계산
                    page = (currentSize - 1) / loadSize + 1,
                    size = loadSize,
                )
                val newUiModels = mapToProductUiModels(page.items)
                val combineProducts = currentProducts + newUiModels

                _uiState.update {
                    it.copy(
                        visibleCount = minOf(it.visibleCount + loadSize, page.totalElements),
                        visibleProducts = combineProducts,
                        hasNext = !page.isLast,
                        sizeInRepo = page.totalElements,
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun syncCartState() {
        viewModelScope.launch {
            val cartItems = cartRepo.getAllCartItems()
            val totalCartCount = cartItems.items.sumOf { it.quantity }
            val recentProducts = recentProductRepo.getRecentProducts()

            _uiState.update { state ->
                val currentProducts = state.visibleProducts.map { it.product }
                val updatedUiModels = mapToProductUiModels(currentProducts)

                state.copy(
                    visibleProducts = updatedUiModels,
                    cartCount = totalCartCount,
                    recentProducts = recentProducts,
                )
            }
        }
    }

    private suspend fun mapToProductUiModels(products: List<Product>): List<ProductUiModel> {
        val cartItems = cartRepo.getAllCartItems()
        val cartQuantityMap: Map<Long, Int> =
            cartItems.items.associate {
                it.product.id to it.quantity
            }
        return products.map { product ->
            ProductUiModel(
                product = product,
                cartQuantity = cartQuantityMap[product.id] ?: 0,
            )
        }
    }

    companion object {
        fun provideFactory(
            applicationContext: Context,
            productRepo: ProductRepository,
            cartRepo: CartRepository,
            recentProductRepo: RecentProductRepository,
            loadSize: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ShoppingViewModel(
                        networkMonitor = NetworkMonitor(applicationContext),
                        productRepo = productRepo,
                        cartRepo = cartRepo,
                        recentProductRepo = recentProductRepo,
                        loadSize = loadSize,
                    ) as T
            }
    }
}
