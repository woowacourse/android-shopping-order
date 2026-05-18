package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.ui.common.recentlyviewed.RecentViewedProductsMapper
import woowacourse.shopping.model.Product

private const val PAGE_SIZE = 20
private const val RECENT_PRODUCT_LIMIT = 10
private const val CART_SYNC_DELAY_MILLIS = 400L

class ShoppingViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val recentProductRepository: RecentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState(productListState = ProductListUiState.Loading))
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var hasNextPage = false
    private var isLoadingProducts = false
    private var loadedProducts: List<Product> = emptyList()
    private val syncJobs = mutableMapOf<Long, Job>()

    init {
        observeNetworkState()
        loadInitialProducts()
    }

    fun reloadVisibleState() {
        if (_uiState.value.productListState is ProductListUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                refreshRecentProducts()
                refreshCartState()
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        productListState = ProductListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    private fun loadInitialProducts() {
        viewModelScope.launch {
            refreshProducts(reset = true)
        }
    }

    private suspend fun refreshProducts(reset: Boolean) {
        if (isLoadingProducts) return
        isLoadingProducts = true
        _uiState.update { currentState ->
            currentState.copy(
                productListState = ProductListUiState.Loading,
            )
        }
        runCatching {
            val targetPage = if (reset) 0 else currentPage + 1
            val pageResult = productRepository.getProducts(page = targetPage, size = PAGE_SIZE)
            val visibleProducts =
                if (reset) {
                    pageResult.items
                } else {
                    loadedProducts + pageResult.items
                }
            val cartState = createCartState(visibleProducts)
            val restoredRecentProducts = getRecentProducts()

            currentPage = pageResult.page
            hasNextPage = pageResult.hasNext
            loadedProducts = visibleProducts

            _uiState.update { currentState ->
                currentState.copy(
                    productListState =
                        ProductListUiState.Content(
                            products = cartState.products,
                            hasNext = hasNextPage,
                        ),
                    recentProducts = restoredRecentProducts,
                    cartQuantity = cartState.cartQuantity,
                )
            }
        }.onFailure { throwable ->
            _uiState.update { currentState ->
                currentState.copy(
                    productListState = ProductListUiState.Error(throwable.message),
                )
            }
        }.also {
            isLoadingProducts = false
        }
    }

    private suspend fun refreshRecentProducts() {
        _uiState.update { currentState ->
            currentState.copy(
                recentProducts = getRecentProducts(),
            )
        }
    }

    private suspend fun refreshCartState() {
        val contentState = _uiState.value.productListState as? ProductListUiState.Content ?: return
        val visibleProducts = contentState.products.map { it.product }
        val cartState = createCartState(visibleProducts)

        _uiState.update { currentState ->
            val latestContent = currentState.productListState as? ProductListUiState.Content ?: return@update currentState
            currentState.copy(
                productListState =
                    latestContent.copy(
                        products = cartState.products,
                    ),
                cartQuantity = cartState.cartQuantity,
            )
        }
    }

    private suspend fun getRecentProducts() =
        recentProductRepository
            .getRecentProducts(RECENT_PRODUCT_LIMIT)
            .let { recentProducts ->
                val recentProductsById = productRepository.findAllByIds(recentProducts.map { it.productId }.toSet())
                RecentViewedProductsMapper.toProducts(
                    recentProducts = recentProducts,
                    productsById = recentProductsById,
                )
            }

    private suspend fun createCartState(visibleProducts: List<woowacourse.shopping.model.Product>): ShoppingCartState {
        val cartQuantity = getCartTotalQuantity()
        val visibleCartItems = cartRepository.getCartItemsByProductIds(visibleProducts.map { it.id }.toSet())
        val quantityByProductId = visibleCartItems.associate { it.productId to it.quantity }

        return ShoppingCartState(
            products =
                ShoppingProductUiStateMapper.toUiStates(
                    products = visibleProducts,
                    quantityByProductId = quantityByProductId,
                ),
            cartQuantity = cartQuantity,
        )
    }

    private suspend fun getCartTotalQuantity(): Int {
        val totalElements = cartRepository.count()
        if (totalElements == 0) return 0

        return cartRepository
            .getCartPage(page = 0, size = totalElements)
            .items
            .sumOf { it.quantity }
    }

    private data class ShoppingCartState(
        val products: List<ShoppingProductUiState>,
        val cartQuantity: Int,
    )

    fun loadMore() {
        val contentState = _uiState.value.productListState as? ProductListUiState.Content ?: return
        if (!contentState.hasNext || isLoadingProducts) return

        viewModelScope.launch {
            refreshProducts(reset = false)
        }
    }

    fun addToCart(productId: Long) = increaseQuantity(productId)

    fun increaseQuantity(productId: Long) = changeQuantity(productId, delta = 1)

    fun decreaseQuantity(productId: Long) = changeQuantity(productId, delta = -1)

    private fun changeQuantity(
        productId: Long,
        delta: Int,
    ) {
        val contentState = _uiState.value.productListState as? ProductListUiState.Content ?: return
        var quantityDelta = 0

        val updatedProducts =
            contentState.products.map { item ->
                if (item.product.id != productId) return@map item

                val nextQuantity = (item.quantity + delta).coerceAtLeast(0)
                quantityDelta = nextQuantity - item.quantity
                item.copy(quantity = nextQuantity)
            }

        if (quantityDelta == 0) return

        _uiState.update { current ->
            val latestContent = current.productListState as? ProductListUiState.Content ?: return@update current
            current.copy(
                productListState = latestContent.copy(products = updatedProducts),
                cartQuantity = (current.cartQuantity + quantityDelta).coerceAtLeast(0),
            )
        }

        scheduleCartSync(productId)
    }

    private fun scheduleCartSync(productId: Long) {
        syncJobs.remove(productId)?.cancel()

        syncJobs[productId] =
            viewModelScope.launch {
                delay(CART_SYNC_DELAY_MILLIS)

                val contentState = _uiState.value.productListState as? ProductListUiState.Content ?: return@launch
                val targetQuantity =
                    contentState.products
                        .firstOrNull { it.product.id == productId }
                        ?.quantity ?: 0

                runCatching {
                    cartRepository.setQuantity(productId, targetQuantity)
                    refreshCartState()
                }.onFailure { throwable ->
                    refreshCartState()
                    _uiState.update { current ->
                        current.copy(
                            productListState = ProductListUiState.Error(throwable.message),
                        )
                    }
                }

                syncJobs.remove(productId)
            }
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            networkMonitor.isNetworkConnected.collect { isConnected ->
                _uiState.update { currentState ->
                    currentState.copy(isNetworkConnected = isConnected)
                }
            }
        }
    }
}
