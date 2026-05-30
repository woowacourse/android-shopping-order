package woowacourse.shopping.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.model.product.Product
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.repository.http.common.RemoteException
import woowacourse.shopping.ui.common.recentlyviewed.RecentViewedProductsMapper

private const val PAGE_SIZE = 20
private const val RECENT_PRODUCT_LIMIT = 10

class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val networkMonitor: NetworkMonitor,
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
            refreshRecentProducts()
            refreshCartState()
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

        val visibleProducts =
            productRepository
                .getProducts(0, visibleCount)
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }.toList()

        val hasNext =
            productRepository
                .hasNext(visibleProducts.count() - 1)
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        val cartState =
            createCartState(visibleProducts)
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        val restoredRecentProducts =
            getRecentProducts()
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

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
    }

    private suspend fun refreshRecentProducts() {
        val recentProducts =
            getRecentProducts()
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        _uiState.update { currentState ->
            currentState.copy(recentProducts = recentProducts)
        }
    }

    private suspend fun refreshCartState() {
        val contentState = _uiState.value.productListState as? ProductListUiState.Content ?: return
        val visibleProducts = contentState.products.map { it.product }

        val cartState =
            createCartState(visibleProducts)
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

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

    private suspend fun getRecentProducts(): Result<List<Product>> {
        val recentProducts = recentProductRepository.getRecentProducts(RECENT_PRODUCT_LIMIT)

        val recentProductsById =
            productRepository
                .findAllByIds(recentProducts.map { it.productId }.toSet())
                .getOrElse { throwable ->
                    return Result.failure(throwable)
                }

        return Result.success(
            RecentViewedProductsMapper.toProducts(
                recentProducts = recentProducts,
                productsById = recentProductsById,
            ),
        )
    }

    private suspend fun createCartState(visibleProducts: List<Product>): Result<ShoppingCartState> {
        val cartQuantity =
            getCartTotalQuantity()
                .getOrElse { throwable ->
                    return Result.failure(throwable)
                }

        val visibleCartItems =
            cartRepository
                .getCartItemsByProductIds(visibleProducts.map { it.id }.toSet())
                .getOrElse { throwable ->
                    return Result.failure(throwable)
                }

        val quantityByProductId = visibleCartItems.associate { it.productId to it.quantity }

        return Result.success(
            ShoppingCartState(
                products =
                    ShoppingProductUiStateMapper.toUiStates(
                        products = visibleProducts,
                        quantityByProductId = quantityByProductId,
                    ),
                cartQuantity = cartQuantity,
            ),
        )
    }

    private suspend fun getCartTotalQuantity(): Result<Int> {
        val totalElements =
            cartRepository
                .count()
                .getOrElse { throwable ->
                    return Result.failure(throwable)
                }

        if (totalElements == 0) {
            return Result.success(0)
        }

        val cartPage =
            cartRepository
                .getCartPage(page = 0, size = totalElements)
                .getOrElse { throwable ->
                    return Result.failure(throwable)
                }

        return Result.success(cartPage.items.sumOf { it.quantity })
    }

    private data class ShoppingCartState(
        val products: List<ShoppingProductUiState>,
        val cartQuantity: Int,
    )

    fun loadMore() {
        val contentState = _uiState.value.productListState as? ProductListUiState.Content ?: return
        if (!contentState.hasNext) return

        viewModelScope.launch {
            productRepository
                .getProducts(contentState.products.size, PAGE_SIZE)
                .onSuccess { newProducts ->
                    val hasNext =
                        productRepository
                            .hasNext(
                                contentState.products.size + newProducts.count() - 1,
                            ).getOrDefault(false)

                    val newCartState =
                        createCartState(newProducts.toList())
                            .getOrElse {
                                updateErrorState(it)
                                return@onSuccess
                            }

                    _uiState.update { currentState ->
                        val latestContent =
                            currentState.productListState as? ProductListUiState.Content
                                ?: return@update currentState
                        currentState.copy(
                            productListState =
                                latestContent.copy(
                                    products = currentState.productListState.products + newCartState.products,
                                    hasNext = hasNext,
                                ),
                            cartQuantity = currentState.cartQuantity + newCartState.cartQuantity,
                        )
                    }
                }.onFailure { throwable ->
                    updateErrorState(throwable)
                }
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
        var targetQuantity = 0

        val updatedProducts =
            contentState.products.map { item ->
                if (item.product.id != productId) return@map item

                targetQuantity = (item.quantity + delta).coerceAtLeast(0)
                quantityDelta = targetQuantity - item.quantity
                item.copy(quantity = targetQuantity)
            }

        if (quantityDelta == 0) return

        _uiState.update { current ->
            val latestContent = current.productListState as? ProductListUiState.Content ?: return@update current
            current.copy(
                productListState = latestContent.copy(products = updatedProducts),
                cartQuantity = (current.cartQuantity + quantityDelta).coerceAtLeast(0),
            )
        }

        viewModelScope.launch {
            cartRepository
                .setQuantity(productId, targetQuantity)
                .onFailure { throwable ->
                    refreshCartState()
                    updateErrorState(throwable)
                }
        }
    }

    private fun updateErrorState(throwable: Throwable) {
        _uiState.update { currentState ->
            currentState.copy(
                productListState = ProductListUiState.Error(throwable.toUserMessage()),
            )
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
