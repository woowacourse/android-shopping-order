package woowacourse.shopping.ui.cart.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.repository.cart.CartPageItem
import woowacourse.shopping.ui.cart.SelectedCartOrder
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.shopping.ShoppingProductUiStateMapper

private const val RECOMMENDED_PRODUCTS_LIMIT = 10
private const val RECOMMENDED_PRODUCTS_FALLBACK_LIMIT = 20

class CartRecommendationViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val recentProductRepository: RecentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartRecommendationUiState())
    val uiState: StateFlow<CartRecommendationUiState> = _uiState.asStateFlow()

    private val recommendedSyncJobs = mutableMapOf<Long, Job>()
    private val orderedProductIds = linkedSetOf<Long>()
    private var excludedProductIds: Set<Long> = emptySet()
    private var baseOrderItemDataByProductId: Map<Long, OrderItemData> = emptyMap()
    private var isSessionStarted = false

    init {
        observeNetworkState()
    }

    fun startOrder(selectedCartOrder: SelectedCartOrder) {
        excludedProductIds = selectedCartOrder.items.map { it.productId }.toSet()
        orderedProductIds.clear()
        orderedProductIds += excludedProductIds
        baseOrderItemDataByProductId =
            selectedCartOrder.items.associate { item ->
                item.productId to OrderItemData(price = item.price, quantity = item.quantity)
            }
        isSessionStarted = true

        _uiState.update { currentState ->
            currentState.copy(
                pendingOrder =
                    PendingOrderUiState(
                        cartItemIds = selectedCartOrder.items.map { it.cartItemId },
                        excludedProductIds = excludedProductIds,
                        selectedCount = selectedCartOrder.items.size,
                        totalPrice = selectedCartOrder.items.sumOf { it.price * it.quantity },
                    ),
                orderErrorMessage = null,
                orderCompletedCount = 0,
            )
        }

        loadRecommendedProducts()
    }

    fun reloadVisibleState() {
        if (!isSessionStarted) return

        viewModelScope.launch {
            runCatching {
                val recommendedProducts = getRecommendedProducts(excludedProductIds)
                _uiState.update { currentState ->
                    currentState.copy(
                        recommendedProducts = recommendedProducts,
                        isRecommendedProductsLoading = false,
                    )
                }
                refreshPendingOrder()
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isRecommendedProductsLoading = false,
                        orderErrorMessage = throwable.message ?: "추천 상품을 불러오지 못했습니다.",
                    )
                }
            }
        }
    }

    fun addRecommendedProduct(productId: Long) {
        changeRecommendedProductQuantity(productId, delta = 1)
    }

    fun increaseRecommendedProductQuantity(productId: Long) {
        changeRecommendedProductQuantity(productId, delta = 1)
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        changeRecommendedProductQuantity(productId, delta = -1)
    }

    fun placeOrder() {
        if (_uiState.value.pendingOrder.cartItemIds.isEmpty() || _uiState.value.isOrdering) return

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isOrdering = true,
                    orderErrorMessage = null,
                )
            }

            runCatching {
                awaitRecommendedSyncs()
                val cartItemIds = _uiState.value.pendingOrder.cartItemIds
                check(cartItemIds.isNotEmpty()) { "주문할 상품이 없습니다." }
                cartRepository.createOrder(cartItemIds)
            }.onSuccess {
                orderedProductIds.clear()
                _uiState.update { currentState ->
                    currentState.copy(
                        pendingOrder = PendingOrderUiState(),
                        isOrdering = false,
                        orderCompletedCount = currentState.orderCompletedCount + 1,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isOrdering = false,
                        orderErrorMessage = throwable.message ?: "주문에 실패했습니다.",
                    )
                }
            }
        }
    }

    fun clearOrderError() {
        _uiState.update { currentState ->
            currentState.copy(orderErrorMessage = null)
        }
    }

    private fun loadRecommendedProducts() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isRecommendedProductsLoading = true,
                )
            }

            runCatching {
                getRecommendedProducts(excludedProductIds)
            }.onSuccess { recommendedProducts ->
                _uiState.update { currentState ->
                    currentState.copy(
                        recommendedProducts = recommendedProducts,
                        isRecommendedProductsLoading = false,
                    )
                }
            }.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        recommendedProducts = emptyList(),
                        isRecommendedProductsLoading = false,
                    )
                }
            }
        }
    }

    private suspend fun getRecommendedProducts(excludedProductIds: Set<Long>): List<ShoppingProductUiState> {
        val recentProductIds =
            recentProductRepository
                .getRecentProducts(RECOMMENDED_PRODUCTS_LIMIT * 2)
                .map { it.productId }
                .distinct()
                .filterNot { it in excludedProductIds }
                .take(RECOMMENDED_PRODUCTS_LIMIT)

        val recentProducts =
            if (recentProductIds.isEmpty()) {
                emptyList()
            } else {
                val productsById = productRepository.findAllByIds(recentProductIds.toSet())
                recentProductIds.mapNotNull(productsById::get)
            }

        val recommendedProducts =
            if (recentProducts.isNotEmpty()) {
                recentProducts
            } else {
                productRepository
                    .getProducts(fromIndex = 0, limit = RECOMMENDED_PRODUCTS_FALLBACK_LIMIT)
                    .toList()
                    .filterNot { it.id in excludedProductIds }
                    .take(RECOMMENDED_PRODUCTS_LIMIT)
            }

        val quantityByProductId =
            cartRepository
                .getCartItemsByProductIds(recommendedProducts.map { it.id }.toSet())
                .associate { it.productId to it.quantity }

        return ShoppingProductUiStateMapper.toUiStates(
            products = recommendedProducts,
            quantityByProductId = quantityByProductId,
        )
    }

    private fun changeRecommendedProductQuantity(
        productId: Long,
        delta: Int,
    ) {
        val currentQuantity =
            _uiState.value.recommendedProducts
                .firstOrNull { it.product.id == productId }
                ?.quantity ?: return
        val targetQuantity = (currentQuantity + delta).coerceAtLeast(0)
        if (targetQuantity == currentQuantity) return

        if (!updateRecommendedProductQuantity(productId, targetQuantity)) return

        recommendedSyncJobs.remove(productId)?.cancel()
        recommendedSyncJobs[productId] =
            viewModelScope.launch {
                runCatching {
                    cartRepository.setQuantity(productId, targetQuantity)
                    if (targetQuantity > 0) {
                        orderedProductIds += productId
                    } else {
                        orderedProductIds -= productId
                    }
                    reloadRecommendedProducts()
                    refreshPendingOrder()
                }.onFailure { throwable ->
                    reloadRecommendedProducts()
                    refreshPendingOrder()
                    _uiState.update { currentState ->
                        currentState.copy(
                            orderErrorMessage = throwable.message ?: "장바구니를 갱신하지 못했습니다.",
                        )
                    }
                }

                recommendedSyncJobs.remove(productId)
            }
    }

    private fun updateRecommendedProductQuantity(
        productId: Long,
        targetQuantity: Int,
    ): Boolean {
        var changed = false
        val updatedProducts =
            _uiState.value.recommendedProducts.map { product ->
                if (product.product.id != productId) return@map product
                if (product.quantity == targetQuantity) return@map product

                changed = true
                product.copy(quantity = targetQuantity)
            }

        if (!changed) return false

        _uiState.update { currentState ->
            currentState.copy(recommendedProducts = updatedProducts)
        }
        return true
    }

    private suspend fun reloadRecommendedProducts() {
        val recommendedProducts = getRecommendedProducts(excludedProductIds)
        _uiState.update { currentState ->
            currentState.copy(
                recommendedProducts = recommendedProducts,
                isRecommendedProductsLoading = false,
            )
        }
    }

    private suspend fun refreshPendingOrder() {
        val pendingOrderExcludedProductIds = _uiState.value.pendingOrder.excludedProductIds
        if (orderedProductIds.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    pendingOrder = PendingOrderUiState(excludedProductIds = pendingOrderExcludedProductIds),
                )
            }
            return
        }

        val cartItems = resolveCartPageItems(orderedProductIds)
        val itemDataByProductId = buildOrderItemDataByProductId()
        val activeCartItems =
            cartItems.filter { cartItem ->
                (itemDataByProductId[cartItem.productId]?.quantity ?: 0) > 0
            }

        orderedProductIds.clear()
        orderedProductIds += activeCartItems.map { it.productId }

        _uiState.update { currentState ->
            currentState.copy(
                pendingOrder =
                    PendingOrderUiState(
                        cartItemIds = activeCartItems.map { it.cartItemId },
                        excludedProductIds = pendingOrderExcludedProductIds,
                        selectedCount = activeCartItems.size,
                        totalPrice =
                            activeCartItems.sumOf { cartItem ->
                                val itemData = itemDataByProductId[cartItem.productId] ?: return@sumOf 0
                                itemData.price * itemData.quantity
                            },
                    ),
            )
        }
    }

    private fun buildOrderItemDataByProductId(): Map<Long, OrderItemData> {
        val itemDataByProductId = linkedMapOf<Long, OrderItemData>()
        itemDataByProductId.putAll(baseOrderItemDataByProductId)

        _uiState.value.recommendedProducts.forEach { product ->
            itemDataByProductId[product.product.id] =
                OrderItemData(
                    price = product.product.price.value,
                    quantity = product.quantity,
                )
        }

        return itemDataByProductId
    }

    private suspend fun awaitRecommendedSyncs() {
        recommendedSyncJobs.values.toList().joinAll()
    }

    private suspend fun resolveCartPageItems(productIds: Set<Long>): List<CartPageItem> {
        if (productIds.isEmpty()) return emptyList()

        val totalCount = cartRepository.count()
        if (totalCount == 0) return emptyList()

        return cartRepository
            .getCartPage(page = 0, size = totalCount)
            .items
            .filter { it.productId in productIds }
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

    private data class OrderItemData(
        val price: Int,
        val quantity: Int,
    )
}
