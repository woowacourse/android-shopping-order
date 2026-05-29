package woowacourse.shopping.ui.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.common.NetworkMonitor
import woowacourse.shopping.di.ShoppingRepositoryProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.common.recentlyviewed.RecentViewedProductsMapper

private const val CART_SYNC_DELAY_MILLIS = 400L

class ProductDetailViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val recentProductRepository: RecentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var syncJob: Job? = null

    init {
        observeNetworkState()
    }

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            runCatching {
                val product = productRepository.findAllByIds(setOf(productId))[productId] ?: return@runCatching
                recentProductRepository.recordView(product.id)
                refreshProductDetail(product.id)
            }.onFailure { throwable ->
                _uiState.value =
                    _uiState.value.copy(
                        isAdding = false,
                        errorMessage = throwable.message,
                    )
            }
        }
    }

    fun addToCart() {
        increaseQuantity()
    }

    fun increaseQuantity() = changeQuantity(delta = 1)

    fun decreaseQuantity() = changeQuantity(delta = -1)

    private fun changeQuantity(delta: Int) {
        val product = _uiState.value.product ?: return
        val nextQuantity = (_uiState.value.quantity + delta).coerceAtLeast(0)
        if (nextQuantity == _uiState.value.quantity) return

        _uiState.update { current ->
            current.copy(
                quantity = nextQuantity,
                isAdding = true,
                errorMessage = null,
            )
        }

        scheduleCartSync(product.id)
    }

    private fun scheduleCartSync(productId: Long) {
        syncJob?.cancel()

        syncJob =
            viewModelScope.launch {
                delay(CART_SYNC_DELAY_MILLIS)

                val targetQuantity = _uiState.value.quantity

                runCatching {
                    cartRepository.setQuantity(productId, targetQuantity)
                    refreshProductDetail(productId)
                }.onFailure { throwable ->
                    refreshProductDetail(productId)
                    _uiState.update { current ->
                        current.copy(
                            isAdding = false,
                            errorMessage = throwable.message,
                        )
                    }
                }
            }
    }

    private suspend fun refreshProductDetail(productId: Long) {
        val product = productRepository.findAllByIds(setOf(productId))[productId] ?: return
        val lastViewedRecentProduct = recentProductRepository.getLatestViewedProductExcluding(productId)
        val lastViewedProductsById =
            productRepository.findAllByIds(
                listOfNotNull(lastViewedRecentProduct?.productId).toSet(),
            )
        val lastViewedProduct =
            RecentViewedProductsMapper.toProduct(
                recentProduct = lastViewedRecentProduct,
                productsById = lastViewedProductsById,
            )
        val quantity =
            cartRepository
                .getCartItemsByProductIds(setOf(productId))
                .firstOrNull()
                ?.quantity ?: 0

        _uiState.value =
            _uiState.value.copy(
                product = product,
                lastViewedProduct = lastViewedProduct,
                quantity = quantity,
                isAdding = false,
                errorMessage = null,
            )
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
