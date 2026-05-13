package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.model.ProductId
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.ui.common.recentlyviewed.RecentViewedProductsMapper

private const val PAGE_SIZE = 20
private const val RECENT_PRODUCT_LIMIT = 10

class ShoppingViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val recentProductRepository: RecentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState(productListState = ProductListUiState.Loading))
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    private var visibleCount = PAGE_SIZE

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
                        productListState = ProductListUiState.Error(throwable.message),
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
                    productListState = ProductListUiState.Error(throwable.message),
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
        val currentProductListState = _uiState.value.productListState
        val contentState = currentProductListState as? ProductListUiState.Content ?: return

        val visibleProducts = contentState.products.map { it.product }
        val cartState = createCartState(visibleProducts)

        _uiState.update { currentState ->
            val lastestContent = currentState.productListState as? ProductListUiState.Content ?: return@update currentState
            currentState.copy(
                productListState =
                    lastestContent.copy(
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
        val cartItems = cartRepository.getCartItems(0, cartRepository.count())
        val cartQuantity = cartItems.sumOf { it.quantity }
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

    fun addToCart(productId: ProductId) = increaseQuantity(productId)

    fun increaseQuantity(productId: ProductId) {
        if (_uiState.value.productListState is ProductListUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                cartRepository.add(productId)
                refreshCartState()
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        productListState = ProductListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    fun decreaseQuantity(productId: ProductId) {
        if (_uiState.value.productListState is ProductListUiState.Loading) return

        viewModelScope.launch {
            runCatching {
                cartRepository.delete(productId)
                refreshCartState()
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        productListState = ProductListUiState.Error(throwable.message),
                    )
                }
            }
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
