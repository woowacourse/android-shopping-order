package woowacourse.shopping.ui.cart.recommendation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
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
import woowacourse.shopping.ui.navigation.CartRecommendation
import woowacourse.shopping.ui.navigation.OrderProduct
import woowacourse.shopping.ui.navigation.OrderProductListType
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.shopping.ShoppingProductUiStateMapper
import kotlin.reflect.typeOf

private const val RECOMMENDED_PRODUCTS_LIMIT = 10

class CartRecommendationViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val route: CartRecommendation =
        savedStateHandle.toRoute(
            typeMap = mapOf(typeOf<List<OrderProduct>>() to OrderProductListType),
        )
    private val initialOrderProducts = route.orderProducts

    private val _uiState = MutableStateFlow(CartRecommendationUiState())
    val uiState: StateFlow<CartRecommendationUiState> = _uiState.asStateFlow()

    private var initialCartProductIds: Set<Long>? = null
    private var excludedProductIds: Set<Long> = emptySet()
    private val recommendedOrderItemDataByProductId = linkedMapOf<Long, OrderItemData>()
    private var isSessionStarted = false

    init {
        observeNetworkState()
        initializeOrder()
    }

    private fun initializeOrder() {
        if (initialOrderProducts.isEmpty()) return

        excludedProductIds = initialOrderProducts.map { it.productId }.toSet()
        recommendedOrderItemDataByProductId.clear()
        isSessionStarted = true
        initialCartProductIds = null

        val totalPrice = initialOrderProducts.sumOf { it.price * it.quantity }

        _uiState.update { currentState ->
            currentState.copy(
                pendingOrder =
                    PendingOrderUiState(
                        cartItemIds = emptyList(),
                        excludedProductIds = excludedProductIds,
                        selectedCount = initialOrderProducts.size,
                        totalPrice = totalPrice,
                    ),
                errorMessage = null,
            )
        }

        loadRecommendedProducts()
    }

    fun reloadVisibleState() {
        if (!isSessionStarted) return

        viewModelScope.launch {
            val recommendedProducts = getRecommendedProducts()
            val currentQuantities = _uiState.value.recommendedProducts.associate { it.product.id to it.quantity }

            val updatedRecommendedProducts =
                recommendedProducts.map { product ->
                    val quantity = currentQuantities[product.product.id] ?: product.quantity
                    product.copy(quantity = quantity)
                }

            _uiState.update { currentState ->
                currentState.copy(
                    recommendedProducts = updatedRecommendedProducts,
                    isRecommendedProductsLoading = false,
                )
            }

            refreshPendingOrder()
        }
    }

    fun addRecommendedProduct(productId: Long) {
        changeRecommendedProductQuantity(productId, delta = 1)
    }

    fun decreaseRecommendedProductQuantity(productId: Long) {
        changeRecommendedProductQuantity(productId, delta = -1)
    }

    fun applyRecommendations() {
        if (_uiState.value.isApplying) return

        val itemData = buildOrderItemDataByProductId().filter { it.value.quantity > 0 }

        if (itemData.isEmpty()) {
            updateError("주문할 상품이 없습니다.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isApplying = true) }

            val recommendedProductIds =
                _uiState.value.recommendedProducts
                    .map { it.product.id }
                    .toSet()
            val recommendedItems = itemData.filter { it.key in recommendedProductIds }

            val allSuccess =
                recommendedItems.all { (productId, data) ->
                    cartRepository.setQuantity(productId, data.quantity).isSuccess
                }

            if (!allSuccess) {
                updateError("장바구니 반영에 실패했습니다.")
                return@launch
            }

            val finalOrderProducts =
                itemData.map { (productId, data) ->
                    OrderProduct(
                        productId = productId,
                        quantity = data.quantity,
                        price = data.price,
                    )
                }

            _uiState.update { currentState ->
                currentState.copy(
                    orderProductsToOrder = finalOrderProducts,
                    isApplying = false,
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    private fun loadRecommendedProducts() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isRecommendedProductsLoading = true,
                )
            }

            val recommendedProducts = getRecommendedProducts()

            _uiState.update { currentState ->
                currentState.copy(
                    recommendedProducts = recommendedProducts,
                    isRecommendedProductsLoading = false,
                )
            }
        }
    }

    private suspend fun getRecommendedProducts(): List<ShoppingProductUiState> {
        val latestViewedProductId =
            recentProductRepository
                .getRecentProducts(limit = 1)
                .firstOrNull()
                ?.productId ?: return emptyList()

        val latestViewedProduct =
            productRepository
                .findAllByIds(setOf(latestViewedProductId))
                .getOrElse { return emptyList() }[latestViewedProductId]
                ?: return emptyList()

        if (latestViewedProduct.category.isBlank()) return emptyList()

        val cartProductIds =
            initialCartProductIds
                ?: resolveCartProductIds().also { initialCartProductIds = it }

        val fetchLimit = RECOMMENDED_PRODUCTS_LIMIT + cartProductIds.size

        val recommendedProducts =
            productRepository
                .getProductsByCategory(
                    category = latestViewedProduct.category,
                    limit = fetchLimit,
                ).getOrElse { return emptyList() }
                .toList()
                .filterNot { it.id in cartProductIds }
                .take(RECOMMENDED_PRODUCTS_LIMIT)

        val quantityByProductId =
            cartRepository
                .getCartItemsByProductIds(recommendedProducts.map { it.id }.toSet())
                .getOrElse { emptyList() }
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
        val currentProduct =
            _uiState.value.recommendedProducts
                .firstOrNull { it.product.id == productId } ?: return
        val currentQuantity = currentProduct.quantity
        val targetQuantity = (currentQuantity + delta).coerceAtLeast(0)
        if (targetQuantity == currentQuantity) return

        val productPrice = currentProduct.product.price.value

        if (!updateRecommendedProductQuantity(productId, targetQuantity)) return

        if (targetQuantity > 0) {
            recommendedOrderItemDataByProductId[productId] =
                OrderItemData(
                    price = productPrice,
                    quantity = targetQuantity,
                )
        } else {
            recommendedOrderItemDataByProductId.remove(productId)
        }

        viewModelScope.launch {
            refreshPendingOrder()
        }
    }

    private fun updateRecommendedProductQuantity(
        productId: Long,
        targetQuantity: Int,
    ): Boolean {
        var changed = false
        _uiState.update { currentState ->
            val updatedProducts =
                currentState.recommendedProducts.map { product ->
                    if (product.product.id != productId) return@map product
                    if (product.quantity == targetQuantity) return@map product

                    changed = true
                    product.copy(quantity = targetQuantity)
                }

            if (changed) {
                currentState.copy(recommendedProducts = updatedProducts)
            } else {
                currentState
            }
        }
        return changed
    }

    private fun refreshPendingOrder() {
        val pendingOrderExcludedProductIds = _uiState.value.pendingOrder.excludedProductIds

        val itemDataByProductId = buildOrderItemDataByProductId()

        val activeItems =
            itemDataByProductId.filter {
                it.value.quantity > 0
            }

        _uiState.update { currentState ->
            currentState.copy(
                pendingOrder =
                    PendingOrderUiState(
                        cartItemIds = emptyList(),
                        excludedProductIds = pendingOrderExcludedProductIds,
                        selectedCount = activeItems.size,
                        totalPrice = activeItems.values.sumOf { it.price * it.quantity },
                    ),
            )
        }
    }

    private fun buildOrderItemDataByProductId(): Map<Long, OrderItemData> {
        val itemDataByProductId = linkedMapOf<Long, OrderItemData>()
        initialOrderProducts.forEach { product ->
            itemDataByProductId[product.productId] =
                OrderItemData(
                    price = product.price,
                    quantity = product.quantity,
                )
        }
        itemDataByProductId.putAll(recommendedOrderItemDataByProductId)

        return itemDataByProductId
    }

    private suspend fun resolveCartProductIds(): Set<Long> =
        cartRepository
            .getCartPage(page = 0, size = Int.MAX_VALUE)
            .getOrElse { return emptySet() }
            .items
            .map { it.productId }
            .toSet()

    private fun updateError(message: String) {
        _uiState.update { currentState ->
            currentState.copy(
                isApplying = false,
                errorMessage = message,
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

    private data class OrderItemData(
        val price: Int,
        val quantity: Int,
    )
}

class CartRecommendationViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return CartRecommendationViewModel(
            savedStateHandle = savedStateHandle,
            productRepository = ShoppingRepositoryProvider.productRepository,
            cartRepository = ShoppingRepositoryProvider.cartRepository,
            recentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
            networkMonitor = ShoppingRepositoryProvider.networkMonitor,
        ) as T
    }
}
