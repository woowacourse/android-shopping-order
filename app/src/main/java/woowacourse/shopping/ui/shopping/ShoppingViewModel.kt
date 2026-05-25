package woowacourse.shopping.ui.shopping

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
import retrofit2.HttpException
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.remote.NetworkMonitor
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentProductRepository
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.ui.common.model.ProductUiModel
import java.io.IOException

class ShoppingViewModel(
    networkMonitor: NetworkMonitor,
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val recentProductRepo: RecentProductRepository,
    private val loadSize: Int,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()
    val isNetworkConnected: StateFlow<Boolean> =
        networkMonitor.isConnected
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = true,
            )

    init {
        loadInitialProducts()
    }

    fun retry() {
        if (_uiState.value.visibleProducts.isEmpty()) {
            loadInitialProducts()
        } else {
            syncCartState()
        }
    }

    private fun loadInitialProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val initialPage = productRepo.getProductPage(page = 1, count = loadSize)
                val uiModels = mapToProductUiModels(initialPage.items)
                val recentProducts = recentProductRepo.getRecentProducts()

                _uiState.update {
                    it.copy(
                        currentPage = initialPage.currentPage,
                        visibleProducts = uiModels,
                        hasNext = initialPage.hasNext,
                        totalProductCount = initialPage.totalCount,
                        recentProducts = recentProducts,
                        errorMessage = null,
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "상품 목록을 불러오지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "상품 목록을 불러오지 못했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun increase(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                cartRepo.add(product)

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
                    state.copy(
                        visibleProducts = updatedProducts,
                        cartCount = cartCount,
                        errorMessage = null,
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니 수량을 변경하지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니 수량을 변경하지 못했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun decrease(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                    state.copy(
                        visibleProducts = updatedProducts,
                        cartCount = cartCount,
                        errorMessage = null,
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니 수량을 변경하지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니 수량을 변경하지 못했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (!currentState.hasNext) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val nextPage = currentState.currentPage + 1
                val pageResult = productRepo.getProductPage(page = nextPage, count = loadSize)
                val newUiModels = mapToProductUiModels(pageResult.items)
                val combineProducts = currentState.visibleProducts + newUiModels

                _uiState.update {
                    it.copy(
                        currentPage = pageResult.currentPage,
                        visibleProducts = combineProducts,
                        hasNext = pageResult.hasNext,
                        totalProductCount = pageResult.totalCount,
                        errorMessage = null,
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "상품을 더 불러오지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "상품을 더 불러오지 못했습니다.")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun syncCartState() {
        viewModelScope.launch {
            try {
                val totalCartCount = cartRepo.getCartCount()
                val recentProducts = recentProductRepo.getRecentProducts()

                _uiState.update { state ->
                    val currentProducts = state.visibleProducts.map { it.product }
                    val updatedUiModels = mapToProductUiModels(currentProducts)

                    state.copy(
                        visibleProducts = updatedUiModels,
                        cartCount = totalCartCount,
                        recentProducts = recentProducts,
                        errorMessage = null,
                    )
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니 정보를 동기화하지 못했습니다.")
                }
            } catch (_: HttpException) {
                _uiState.update {
                    it.copy(errorMessage = "장바구니 정보를 동기화하지 못했습니다.")
                }
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
            container: AppContainer,
            networkMonitor: NetworkMonitor,
            loadSize: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ShoppingViewModel(
                        networkMonitor = networkMonitor,
                        productRepo = container.productRepository,
                        cartRepo = container.cartRepository,
                        recentProductRepo = container.recentProductRepository,
                        loadSize = loadSize,
                    ) as T
            }
    }
}
