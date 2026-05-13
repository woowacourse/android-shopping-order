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
import woowacourse.shopping.repository.ShoppingRepositoryProvider

private const val PAGE_SIZE = 5
private const val CART_SYNC_DELAY_MILLIS = 400L

class CartViewModel(
    private val productRepository: ProductRepository = ShoppingRepositoryProvider.productRepository,
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState(cartListState = CartListUiState.Loading))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val syncJobs = mutableMapOf<Long, Job>()

    init {
        observeNetworkState()
        loadPage(1)
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
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        val item = contentState.items.firstOrNull { it.productId == productId } ?: return

        if (!updateLocalQuantity(productId, targetQuantity = item.quantity + 1)) return
        scheduleCartSync(productId)
    }

    fun decreaseQuantity(productId: Long) {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        val item = contentState.items.firstOrNull { it.productId == productId } ?: return
        val targetQuantity = (item.quantity - 1).coerceAtLeast(0)

        if (!updateLocalQuantity(productId, targetQuantity = targetQuantity)) return
        if (targetQuantity == 0) {
            clearDeselection(productId)
        }
        scheduleCartSync(productId)
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

        if (!changed) return false

        _uiState.update { current ->
            val latestContent = current.cartListState as? CartListUiState.Content ?: return@update current
            current.copy(
                cartListState = latestContent.copy(items = updatedItems),
            )
        }
        return true
    }

    private fun scheduleCartSync(productId: Long) {
        syncJobs.remove(productId)?.cancel()

        syncJobs[productId] =
            viewModelScope.launch {
                delay(CART_SYNC_DELAY_MILLIS)

                val currentPage = (_uiState.value.cartListState as? CartListUiState.Content)?.currentPage ?: 1
                val targetQuantity =
                    (_uiState.value.cartListState as? CartListUiState.Content)
                        ?.items
                        ?.firstOrNull { it.productId == productId }
                        ?.quantity ?: 0

                runCatching {
                    cartRepository.setQuantity(productId, targetQuantity)
                    updatePage(currentPage)
                }.onFailure { throwable ->
                    updatePage(currentPage)
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
