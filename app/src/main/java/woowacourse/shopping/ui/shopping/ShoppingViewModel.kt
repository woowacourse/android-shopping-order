package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import woowacourse.shopping.repository.http.common.RemoteException
import woowacourse.shopping.ui.common.recentlyviewed.RecentViewedProductsMapper

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

    private var visibleCount = PAGE_SIZE
    private val syncJobs = mutableMapOf<Long, Job>()

    init {
        observeNetworkState()
        loadProducts()
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
                        productListState = ProductListUiState.Error(throwable.toUserMessage()),
                    )
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            refreshProducts()
        }
    }

    suspend fun refreshProducts() {
        _uiState.update { currentState ->
            currentState.copy(
                productListState = ProductListUiState.Loading,
            )
        }
        runCatching {
            val visibleProducts = productRepository.getProducts(0, visibleCount).toList()
            val hasNext = productRepository.hasNext(visibleProducts.count() - 1)
            val cartState = createCartState(visibleProducts)
            val restoredRecentProducts = getRecentProducts()

            _uiState.update { currentState ->
                currentState.copy(
                    productListState =
                        ProductListUiState.Content(
                            products = cartState.products,
                            hasNext = hasNext,
                        ),
                    recentProducts = restoredRecentProducts,
                    cartQuantity = cartState.cartQuantity,
                )
            }
        }.onFailure { throwable ->
            _uiState.update { currentState ->
                currentState.copy(
                    productListState = ProductListUiState.Error(throwable.toUserMessage()),
                )
            }
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
        if (!contentState.hasNext) return

        visibleCount = minOf(visibleCount + PAGE_SIZE, productRepository.size)
        loadProducts()
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
                }.onFailure { throwable ->
                    refreshCartState()
                    _uiState.update { current ->
                        current.copy(
                            productListState = ProductListUiState.Error(throwable.toUserMessage()),
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

class ShoppingViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ShoppingViewModel(
            productRepository = ShoppingRepositoryProvider.productRepository,
            cartRepository = ShoppingRepositoryProvider.cartRepository,
            recentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
            networkMonitor = ShoppingRepositoryProvider.networkMonitor,
        ) as T
}

private fun Throwable.toUserMessage(): String =
    when (this) {
        is RemoteException -> userMessage
        else -> "알 수 없는 오류가 발생했습니다."
    }
