package woowacourse.shopping.ui.productlist

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import woowacourse.shopping.data.local.datastore.VisitStore
import woowacourse.shopping.data.remote.retrofit.repository.ProductRetrofitRepository
import woowacourse.shopping.data.remote.retrofit.toApiFailure
import woowacourse.shopping.data.remote.retrofit.toUserMessage
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.domain.repository.ShoppingItemRepository

class ProductListViewModel(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val visitStore: VisitStore,
    private val productRepository: ProductRetrofitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    private val _event = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ProductListEvent> = _event.asSharedFlow()

    private val productRequestMutex = Mutex()
    private var hasLoadedProductsOnce: Boolean = false
    private var lastProductsLoadedElapsedMs: Long = 0L
    private val productPageStateHolder = ProductPageStateHolder(shoppingItems = emptyList())
    private var allShoppingItems: List<ShoppingItem> = shoppingItemRepository.shoppingItems.value
    private var recentViewedProductIds: List<Long> = visitStore.recentVisitedProductIds.value

    init {
        productPageStateHolder.updateItems(allShoppingItems)
        publishUiState()
        observeSources()
    }

    fun requestProduct(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        category: String? = null,
        force: Boolean = false,
    ) {
        if (shouldSkipProductRequest(force = force)) return

        viewModelScope.launch {
            productRequestMutex.withLock {
                if (shouldSkipProductRequest(force = force)) return@withLock

                publishUiState(
                    isLoading = true,
                    errorMessage = null,
                )

                runCatching {
                    requestAllProducts(
                        startPage = page,
                        size = size,
                        category = category,
                    )
                }.onSuccess { loadedProducts ->
                    shoppingItemRepository.replaceProducts(loadedProducts)
                    productPageStateHolder.restoreCurrentPage(DEFAULT_PAGE)
                    markProductsLoaded()
                    publishUiState(
                        isLoading = false,
                        errorMessage = null,
                        hasLoadedProducts = true,
                    )
                }.onFailure { throwable ->
                    publishUiState(
                        isLoading = false,
                        errorMessage =
                            throwable
                                .toApiFailure()
                                .toUserMessage(defaultMessage = "상품 목록을 불러오지 못했습니다."),
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        if (!productPageStateHolder.canMoveToNextPage()) return
        productPageStateHolder.nextPage()
        publishUiState()
    }

    private suspend fun requestAllProducts(
        startPage: Int,
        size: Int,
        category: String?,
    ): List<Product> {
        val loadedProducts = mutableListOf<Product>()
        var page = startPage
        var hasNextPage: Boolean
        do {
            val pageResult =
                productRepository.requestProductPage(
                    page = page,
                    size = size,
                    category = category,
                )
            loadedProducts += pageResult.products
            hasNextPage = pageResult.hasNextPage
            page += 1
        } while (hasNextPage)
        return loadedProducts
    }

    fun onProductClick(productId: Long) {
        _event.tryEmit(ProductListEvent.NavigateToDetailProduct(productId))
    }

    fun onNavigateToCartClick() {
        _event.tryEmit(ProductListEvent.NavigateToShoppingCart)
    }

    private fun observeSources() {
        viewModelScope.launch {
            shoppingItemRepository.shoppingItems.collect { latestShoppingItems ->
                allShoppingItems = latestShoppingItems
                productPageStateHolder.updateItems(latestShoppingItems)
                publishUiState()
            }
        }
        viewModelScope.launch {
            visitStore.recentVisitedProductIds.collect { latestRecentViewedIds ->
                recentViewedProductIds = latestRecentViewedIds
                publishUiState()
            }
        }
    }

    private fun publishUiState(
        isLoading: Boolean = _uiState.value.isLoading,
        errorMessage: String? = _uiState.value.errorMessage,
        hasLoadedProducts: Boolean = _uiState.value.hasLoadedProducts,
    ) {
        _uiState.value =
            createUiState(
                isLoading = isLoading,
                errorMessage = errorMessage,
                hasLoadedProducts = hasLoadedProducts,
            )
    }

    private fun createUiState(
        isLoading: Boolean,
        errorMessage: String?,
        hasLoadedProducts: Boolean,
    ): ProductListUiState {
        val shoppingItemByProductId = allShoppingItems.associateBy { shoppingItem -> shoppingItem.getProductId() }
        return ProductListUiState(
            isLoading = isLoading,
            errorMessage = errorMessage,
            hasLoadedProducts = hasLoadedProducts,
            shoppingItems = productPageStateHolder.getItems(),
            recentViewedShoppingItems = recentViewedProductIds.mapNotNull { productId -> shoppingItemByProductId[productId] },
            shoppingCartTotalCount = allShoppingItems.sumOf { shoppingItem -> shoppingItem.getQuantity() },
            canLoadNextPage = productPageStateHolder.canMoveToNextPage(),
        )
    }

    private fun shouldSkipProductRequest(force: Boolean): Boolean {
        if (force) return false
        if (!hasLoadedProductsOnce) return false
        return isProductsCacheFresh()
    }

    private fun isProductsCacheFresh(): Boolean =
        SystemClock.elapsedRealtime() - lastProductsLoadedElapsedMs < PRODUCTS_CACHE_DURATION_MS

    private fun markProductsLoaded() {
        hasLoadedProductsOnce = true
        lastProductsLoadedElapsedMs = SystemClock.elapsedRealtime()
    }

    data class ProductListUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val hasLoadedProducts: Boolean = false,
        val shoppingItems: List<ShoppingItem> = emptyList(),
        val recentViewedShoppingItems: List<ShoppingItem> = emptyList(),
        val shoppingCartTotalCount: Int = 0,
        val canLoadNextPage: Boolean = false,
    )

    sealed interface ProductListEvent {
        data class NavigateToDetailProduct(
            val productId: Long,
        ) : ProductListEvent

        data object NavigateToShoppingCart : ProductListEvent
    }

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val PRODUCTS_CACHE_DURATION_MS = 30_000L
    }
}
