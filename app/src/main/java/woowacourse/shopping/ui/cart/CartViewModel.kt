package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.ui.shopping.ShoppingProductUiState
import woowacourse.shopping.ui.shopping.ShoppingProductUiStateMapper

private const val PAGE_SIZE = 5
private const val CART_SYNC_DELAY_MILLIS = 400L
private const val RECOMMENDED_PRODUCTS_LIMIT = 10
private const val RECOMMENDED_PRODUCTS_FALLBACK_LIMIT = 20

class CartViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val recentProductRepository: RecentProductRepository = ShoppingRepositoryProvider.recentProductRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState(cartListState = CartListUiState.Loading))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val syncJobs = mutableMapOf<Long, Job>()

    init {
        observeNetworkState()
        loadPage(1)
    }

    fun reloadVisibleState() {
        val currentPage = (_uiState.value.cartListState as? CartListUiState.Content)?.currentPage ?: 1

        viewModelScope.launch {
            runCatching {
                updatePage(currentPage)
                refreshRecommendedProductsIfNeeded()
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartListState = CartListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    fun loadPreviousPage() {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        if (!contentState.hasPrevious) return

        loadPage(contentState.currentPage - 1)
    }

    fun loadNextPage() {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        if (!contentState.hasNext) return

        loadPage(contentState.currentPage + 1)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    cartListState = CartListUiState.Loading,
                )
            }
            runCatching {
                updatePage(page)
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartListState = CartListUiState.Error(throwable.message),
                    )
                }
            }
        }
    }

    private suspend fun updatePage(page: Int) {
        val requestedPage = page.coerceAtLeast(1)
        val cartPageResult = cartRepository.getCartPage(page = requestedPage - 1, size = PAGE_SIZE)
        val currentPage = if (cartPageResult.totalPages == 0) 1 else cartPageResult.page + 1
        val productMap = productRepository.findAllByIds(cartPageResult.items.map { it.productId }.toSet())

        val items =
            CartItemUiModelMapper.toUiModelsFromCartPage(
                cartItems = cartPageResult.items,
                productsById = productMap,
                deselectedProductIds = _uiState.value.deselectedProductIds,
            )

        _uiState.update { currentState ->
            currentState.copy(
                cartListState =
                    CartListUiState.Content(
                        items = items,
                        currentPage = currentPage,
                        totalPages = cartPageResult.totalPages,
                        hasPrevious = currentPage > 1,
                        hasNext = currentPage < cartPageResult.totalPages,
                    ),
            )
        }
    }

    fun delete(productId: Long) {
        if (!updateLocalQuantity(productId, targetQuantity = 0)) return

        clearDeselection(productId)
        scheduleCartSync(productId)
    }

    fun toggleItemSelection(
        productId: Long,
        isSelected: Boolean,
    ) {
        _uiState.update { currentState ->
            val updatedDeselectedProductIds =
                if (isSelected) {
                    currentState.deselectedProductIds - productId
                } else {
                    currentState.deselectedProductIds + productId
                }

            currentState.copy(deselectedProductIds = updatedDeselectedProductIds)
        }
        refreshVisibleSelections()
    }

    fun increaseQuantity(productId: Long) {
        val quantity = findQuantity(productId) ?: return
        if (!updateLocalQuantity(productId, targetQuantity = quantity + 1)) return
        scheduleCartSync(productId)
    }

    fun decreaseQuantity(productId: Long) {
        val quantity = findQuantity(productId) ?: return
        val targetQuantity = (quantity - 1).coerceAtLeast(0)

        if (!updateLocalQuantity(productId, targetQuantity = targetQuantity)) return
        if (targetQuantity == 0) {
            clearDeselection(productId)
        }
        scheduleCartSync(productId)
    }

    fun loadRecommendedProducts(excludedProductIds: Set<Long>) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isRecommendedProductsLoading = true,
                    recommendedProductFilterIds = excludedProductIds,
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

    fun prepareOrder(): Boolean {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return false
        val selectedItems = contentState.items.filter { it.isSelected }
        if (selectedItems.isEmpty()) return false

        val excludedProductIds = selectedItems.map { it.productId }.toSet()
        _uiState.update { currentState ->
            currentState.copy(
                pendingOrder =
                    PendingOrderUiState(
                        cartItemIds = selectedItems.map { it.cartItemId },
                        excludedProductIds = excludedProductIds,
                        selectedCount = selectedItems.size,
                        totalPrice = selectedItems.sumOf { it.price * it.quantity },
                    ),
                orderErrorMessage = null,
            )
        }
        loadRecommendedProducts(excludedProductIds)
        return true
    }

    fun placeOrder() {
        val cartItemIds = _uiState.value.pendingOrder.cartItemIds
        if (cartItemIds.isEmpty() || _uiState.value.isOrdering) return

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isOrdering = true,
                    orderErrorMessage = null,
                )
            }

            runCatching {
                cartRepository.createOrder(cartItemIds)
                updatePage(1)
                refreshRecommendedProductsIfNeeded()
            }.onSuccess {
                _uiState.update { currentState ->
                    currentState.copy(
                        pendingOrder = PendingOrderUiState(),
                        isOrdering = false,
                        orderCompletedCount = currentState.orderCompletedCount + 1,
                        deselectedProductIds = currentState.deselectedProductIds - currentState.pendingOrder.excludedProductIds,
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

    private suspend fun refreshRecommendedProductsIfNeeded() {
        val filterIds = _uiState.value.recommendedProductFilterIds
        val shouldRefresh =
            filterIds.isNotEmpty() ||
                _uiState.value.recommendedProducts.isNotEmpty() ||
                _uiState.value.isRecommendedProductsLoading

        if (!shouldRefresh) return

        _uiState.update { currentState ->
            currentState.copy(isRecommendedProductsLoading = true)
        }

        runCatching {
            getRecommendedProducts(filterIds)
        }.onSuccess { recommendedProducts ->
            _uiState.update { currentState ->
                currentState.copy(
                    recommendedProducts = recommendedProducts,
                    isRecommendedProductsLoading = false,
                )
            }
        }.onFailure {
            _uiState.update { currentState ->
                currentState.copy(isRecommendedProductsLoading = false)
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

    private fun updateLocalQuantity(
        productId: Long,
        targetQuantity: Int,
    ): Boolean {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return false
        var changed = false

        val updatedItems =
            contentState.items.mapNotNull { item ->
                if (item.productId != productId) return@mapNotNull item
                if (item.quantity == targetQuantity) return@mapNotNull item

                changed = true
                if (targetQuantity == 0) {
                    null
                } else {
                    item.copy(quantity = targetQuantity)
                }
            }

        val updatedRecommendedProducts =
            _uiState.value.recommendedProducts.map { product ->
                if (product.product.id != productId) return@map product
                if (product.quantity == targetQuantity) return@map product

                changed = true
                product.copy(quantity = targetQuantity)
            }

        if (!changed) return false

        _uiState.update { current ->
            val latestContent = current.cartListState as? CartListUiState.Content ?: return@update current
            current.copy(
                cartListState = latestContent.copy(items = updatedItems),
                recommendedProducts = updatedRecommendedProducts,
            )
        }
        return true
    }

    private fun findQuantity(productId: Long): Int? {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content
        val cartItemQuantity = contentState?.items?.firstOrNull { it.productId == productId }?.quantity
        if (cartItemQuantity != null) return cartItemQuantity

        return _uiState.value.recommendedProducts
            .firstOrNull { it.product.id == productId }
            ?.quantity
    }

    private fun scheduleCartSync(productId: Long) {
        syncJobs.remove(productId)?.cancel()

        syncJobs[productId] =
            viewModelScope.launch {
                delay(CART_SYNC_DELAY_MILLIS)

                val currentPage = (_uiState.value.cartListState as? CartListUiState.Content)?.currentPage ?: 1
                val targetQuantity = findQuantity(productId) ?: 0

                runCatching {
                    cartRepository.setQuantity(productId, targetQuantity)
                    updatePage(currentPage)
                    refreshRecommendedProductsIfNeeded()
                }.onFailure { throwable ->
                    updatePage(currentPage)
                    refreshRecommendedProductsIfNeeded()
                    _uiState.update { currentState ->
                        currentState.copy(
                            cartListState = CartListUiState.Error(throwable.message),
                        )
                    }
                }

                syncJobs.remove(productId)
            }
    }

    private fun clearDeselection(productId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                deselectedProductIds = currentState.deselectedProductIds - productId,
            )
        }
    }

    private fun refreshVisibleSelections() {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        val deselectedProductIds = _uiState.value.deselectedProductIds

        _uiState.update { currentState ->
            currentState.copy(
                cartListState =
                    contentState.copy(
                        items =
                            contentState.items.map { item ->
                                item.copy(isSelected = item.productId !in deselectedProductIds)
                            },
                    ),
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
