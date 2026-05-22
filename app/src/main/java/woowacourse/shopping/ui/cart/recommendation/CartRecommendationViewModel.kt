package woowacourse.shopping.ui.cart.recommendation

import CartRecommendation
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
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
import woowacourse.shopping.repository.http.common.RemoteException
import woowacourse.shopping.repository.query.CartPageItem
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.shopping.ShoppingProductUiStateMapper

private const val RECOMMENDED_PRODUCTS_LIMIT = 10

class CartRecommendationViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val route: CartRecommendation = savedStateHandle.toRoute()
    private val selectedCartItemIds = route.selectedCartItemIds.toSet()

    private val _uiState = MutableStateFlow(CartRecommendationUiState())
    val uiState: StateFlow<CartRecommendationUiState> = _uiState.asStateFlow()

    private val recommendedSyncJobs = mutableMapOf<Long, Job>()
    private val orderedProductIds = linkedSetOf<Long>()
    private var excludedProductIds: Set<Long> = emptySet()
    private var baseOrderItemDataByProductId: Map<Long, OrderItemData> = emptyMap()
    private val recommendedOrderItemDataByProductId = linkedMapOf<Long, OrderItemData>()
    private var isSessionStarted = false
    private var initialCartProductIds: Set<Long>? = null

    init {
        observeNetworkState()
        startOrder()
    }

    fun startOrder() {
        if (selectedCartItemIds.isEmpty()) return

        viewModelScope.launch {
            val totalCount = cartRepository.count().getOrDefault(0)
            if (totalCount == 0) return@launch

            val allCartItems =
                cartRepository
                    .getCartPage(
                        page = 0,
                        size = totalCount,
                    ).getOrNull()
                    ?.items ?: return@launch

            val selectedCartItems =
                allCartItems.filter {
                    it.cartItemId in selectedCartItemIds
                }
            if (selectedCartItems.isEmpty()) return@launch

            val productIds =
                selectedCartItems
                    .map {
                        it.productId
                    }.toSet()

            val productsById =
                productRepository
                    .findAllByIds(productIds)
                    .getOrDefault(emptyMap())

            excludedProductIds = productIds
            orderedProductIds.clear()
            orderedProductIds += excludedProductIds

            baseOrderItemDataByProductId =
                selectedCartItems.associate { item ->
                    val price = productsById[item.productId]?.price?.value ?: 0
                    item.productId to OrderItemData(price, item.quantity)
                }
            recommendedOrderItemDataByProductId.clear()
            isSessionStarted = true
            initialCartProductIds = null

            val totalPrice =
                selectedCartItems.sumOf { item ->
                    val price = productsById[item.productId]?.price?.value ?: 0
                    price * item.quantity
                }

            _uiState.update { currentState ->
                currentState.copy(
                    pendingOrder =
                        PendingOrderUiState(
                            cartItemIds = selectedCartItems.map { it.cartItemId },
                            excludedProductIds = excludedProductIds,
                            selectedCount = selectedCartItemIds.size,
                            totalPrice = totalPrice,
                        ),
                    orderErrorMessage = null,
                    orderCompletedCount = 0,
                )
            }

            loadRecommendedProducts()
        }
    }

    fun reloadVisibleState() {
        if (!isSessionStarted) return

        viewModelScope.launch {
            val recommendedProducts = getRecommendedProducts()

            _uiState.update { currentState ->
                currentState.copy(
                    recommendedProducts = recommendedProducts,
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

    fun placeOrder() {
        viewModelScope.launch {
            if (_uiState.value.isOrdering) return@launch

            _uiState.update {
                it.copy(
                    isOrdering = true,
                    orderErrorMessage = null,
                )
            }

            val additionalCartItemIds = mutableListOf<Long>()

            for ((productId, orderData) in recommendedOrderItemDataByProductId) {
                val result = cartRepository.setQuantity(productId, orderData.quantity)
                if (result.isFailure) {
                    updateOrderError("추천 상품을 주문할 수 없습니다.")
                    return@launch
                }
            }

            if (recommendedOrderItemDataByProductId.isNotEmpty()) {
                val totalCount = cartRepository.count().getOrDefault(0)
                val allCartItems =
                    cartRepository
                        .getCartPage(0, totalCount)
                        .getOrNull()
                        ?.items ?: emptyList()

                recommendedOrderItemDataByProductId.keys.forEach { addedProductId ->
                    val newCartItemId =
                        allCartItems
                            .find {
                                it.productId == addedProductId
                            }?.cartItemId

                    if (newCartItemId != null) {
                        additionalCartItemIds.add(newCartItemId)
                    }
                }

                val baseCartItemIds = _uiState.value.pendingOrder.cartItemIds
                val finalCartItemIdsToOrder = baseCartItemIds + additionalCartItemIds

                if (finalCartItemIdsToOrder.isEmpty()) {
                    updateOrderError("주문할 상품이 없습니다")
                    return@launch
                }

                cartRepository
                    .createOrder(finalCartItemIdsToOrder)
                    .onSuccess {
                        orderedProductIds.clear()
                        recommendedOrderItemDataByProductId.clear()
                        _uiState.update { currentState ->
                            currentState.copy(
                                pendingOrder = PendingOrderUiState(),
                                isOrdering = false,
                                orderCompletedCount = currentState.orderCompletedCount + 1,
                            )
                        }
                    }.onFailure { throwable ->
                        updateOrderError(throwable.toUserMessage("주문에 실패했습니다."))
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
        val currentQuantity =
            _uiState.value.recommendedProducts
                .firstOrNull { it.product.id == productId }
                ?.quantity ?: return
        val targetQuantity = (currentQuantity + delta).coerceAtLeast(0)
        if (targetQuantity == currentQuantity) return

        val productPrice =
            _uiState.value.recommendedProducts
                .firstOrNull { it.product.id == productId }
                ?.product
                ?.price
                ?.value ?: return

        if (!updateRecommendedProductQuantity(productId, targetQuantity)) return

        if (targetQuantity > 0) {
            orderedProductIds += productId
            recommendedOrderItemDataByProductId[productId] =
                OrderItemData(
                    price = productPrice,
                    quantity = targetQuantity,
                )
        } else {
            orderedProductIds -= productId
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
        val recommendedProducts = getRecommendedProducts()
        _uiState.update { currentState ->
            currentState.copy(
                recommendedProducts = recommendedProducts,
                isRecommendedProductsLoading = false,
            )
        }
    }

    private suspend fun refreshPendingOrder() {
        val pendingOrderExcludedProductIds = _uiState.value.pendingOrder.excludedProductIds

        val itemDataByProductId = buildOrderItemDataByProductId()

        val activeItems =
            itemDataByProductId.filter {
                it.value.quantity > 0
            }

        val cartItems = resolveCartPageItems(activeItems.keys)

        _uiState.update { currentState ->
            currentState.copy(
                pendingOrder =
                    PendingOrderUiState(
                        cartItemIds = cartItems.map { it.cartItemId },
                        excludedProductIds = pendingOrderExcludedProductIds,
                        selectedCount = activeItems.size,
                        totalPrice = activeItems.values.sumOf { it.price * it.quantity },
                    ),
            )
        }
    }

    private fun buildOrderItemDataByProductId(): Map<Long, OrderItemData> {
        val itemDataByProductId = linkedMapOf<Long, OrderItemData>()
        itemDataByProductId.putAll(baseOrderItemDataByProductId)
        itemDataByProductId.putAll(recommendedOrderItemDataByProductId)

        _uiState.value.recommendedProducts.forEach { product ->
            itemDataByProductId[product.product.id] =
                OrderItemData(
                    price = product.product.price.value,
                    quantity = product.quantity,
                )
        }

        return itemDataByProductId
    }

    private suspend fun resolveCartProductIds(): Set<Long> =
        resolveCartPageItems(productIds = null)
            .map { it.productId }
            .toSet()

    private suspend fun awaitRecommendedSyncs() {
        recommendedSyncJobs.values.toList().joinAll()
    }

    private suspend fun resolveCartPageItems(productIds: Set<Long>?): List<CartPageItem> {
        if (productIds?.isEmpty() == true) return emptyList()
        val totalCount =
            cartRepository
                .count()
                .getOrElse { return emptyList() }
        if (totalCount == 0) return emptyList()

        return cartRepository
            .getCartPage(page = 0, size = totalCount)
            .getOrElse { return emptyList() }
            .items
            .filter { cartItem ->
                productIds == null || cartItem.productId in productIds
            }
    }

    private fun updateOrderError(message: String) {
        _uiState.update { currentState ->
            currentState.copy(
                isOrdering = false,
                orderErrorMessage = message,
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

private fun Throwable.toUserMessage(defaultMessage: String): String =
    when (this) {
        is RemoteException -> userMessage
        else -> message ?: defaultMessage
    }
