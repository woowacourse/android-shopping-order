package woowacourse.shopping.ui.cart.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.repository.http.common.RemoteException
import woowacourse.shopping.ui.cart.SelectedCartOrder
import woowacourse.shopping.ui.cart.SelectedCartOrderItem
import woowacourse.shopping.ui.cart.list.uistate.CartItemUiModelMapper
import woowacourse.shopping.ui.cart.list.uistate.CartListUiState
import woowacourse.shopping.ui.cart.list.uistate.CartUiState

private const val PAGE_SIZE = 5

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState(cartListState = CartListUiState.Loading))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeNetworkState()
        loadPage(1)
        viewModelScope.launch { calculateTotals() }
    }

    fun reloadVisibleState() {
        val currentPage =
            withContentState(defaultValue = 1) { contentState ->
                contentState.currentPage
            }

        viewModelScope.launch {
            updatePage(currentPage)
            calculateTotals()
        }
    }

    fun loadPreviousPage() {
        withContentState { contentState ->
            if (!contentState.hasPrevious) return
            loadPage(contentState.currentPage - 1)
        }
    }

    fun loadNextPage() {
        withContentState { contentState ->
            if (!contentState.hasNext) return
            loadPage(contentState.currentPage + 1)
        }
    }

    suspend fun createSelectedCartOrder(): SelectedCartOrder? {
        val totalCount = cartRepository.count().getOrDefault(0)
        if (totalCount == 0) return null

        val allCartItems =
            cartRepository
                .getCartPage(
                    page = 0,
                    size = totalCount,
                ).getOrNull()
                ?.items ?: return null

        val productIds = allCartItems.map { it.productId }.toSet()
        val productsById =
            productRepository
                .findAllByIds(productIds)
                .getOrDefault(emptyMap())

        val deselectedIds = _uiState.value.deselectedProductIds
        val selectedItems =
            allCartItems.filter {
                it.productId !in deselectedIds
            }

        if (selectedItems.isEmpty()) return null

        return SelectedCartOrder(
            items =
                selectedItems.map { item ->
                    SelectedCartOrderItem(
                        cartItemId = item.cartItemId,
                        productId = item.productId,
                        price = productsById[item.productId]?.price?.value ?: 0,
                        quantity = item.quantity,
                    )
                },
        )
    }

    fun delete(productId: Long) {
        updateQuantity(productId = productId, targetQuantity = 0)
        clearDeselection(productId)
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

        viewModelScope.launch {
            calculateTotals()
        }
        refreshVisibleSelections()
    }

    fun increaseQuantity(productId: Long) {
        val quantity = findQuantity(productId) ?: return
        updateQuantity(productId, quantity + 1)
    }

    fun decreaseQuantity(productId: Long) {
        val quantity = findQuantity(productId) ?: return
        val targetQuantity = (quantity - 1).coerceAtLeast(0)

        updateQuantity(productId = productId, targetQuantity = targetQuantity)
    }

    fun toggleAllSelection(isSelected: Boolean) {
        viewModelScope.launch {
            if (isSelected) {
                _uiState.update { it.copy(deselectedProductIds = emptySet()) }
            } else {
                val totalCount = cartRepository.count().getOrDefault(0)
                val allIds =
                    cartRepository
                        .getCartPage(0, totalCount)
                        .getOrNull()
                        ?.items
                        ?.map {
                            it.productId
                        }?.toSet() ?: emptySet()

                _uiState.update { it.copy(deselectedProductIds = allIds) }
            }
            calculateTotals()
            refreshVisibleSelections()
        }
    }

    suspend fun getSelectedCartItemIds(): List<Long> =
        createSelectedCartOrder()?.items?.map {
            it.cartItemId
        } ?: emptyList()

    private fun updateQuantity(
        productId: Long,
        targetQuantity: Int,
    ) {
        val currentPage =
            withContentState(defaultValue = 1) { contentState ->
                contentState.currentPage
            }

        if (!updateLocalQuantity(productId, targetQuantity)) return

        if (targetQuantity == 0) {
            clearDeselection(productId)
        }

        viewModelScope.launch {
            cartRepository
                .setQuantity(productId, targetQuantity)
                .onSuccess {
                    calculateTotals()

                    withContentState { contentState ->
                        if (contentState.items.isEmpty() && contentState.hasPrevious) {
                            loadPage(contentState.currentPage - 1)
                        } else {
                            updatePage(contentState.currentPage)
                        }
                    }
                }.onFailure { throwable ->
                    updatePage(currentPage)
                    updateErrorState(throwable)
                }
        }
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    cartListState = CartListUiState.Loading,
                )
            }
            updatePage(page)
        }
    }

    private suspend fun updatePage(page: Int) {
        val requestedPage = page.coerceAtLeast(1)

        val cartPageResult =
            cartRepository
                .getCartPage(page = requestedPage - 1, size = PAGE_SIZE)
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        val productMap =
            productRepository
                .findAllByIds(cartPageResult.items.map { it.productId }.toSet())
                .getOrElse { throwable ->
                    updateErrorState(throwable)
                    return
                }

        val currentPage = if (cartPageResult.totalPages == 0) 1 else cartPageResult.page + 1

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

    private fun updateLocalQuantity(
        productId: Long,
        targetQuantity: Int,
    ): Boolean =
        withContentState(false) { contentState ->
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

    private fun findQuantity(productId: Long): Int? =
        (_uiState.value.cartListState as? CartListUiState.Content)
            ?.items
            ?.firstOrNull { it.productId == productId }
            ?.quantity

    private fun updateErrorState(throwable: Throwable) {
        _uiState.update { currentState ->
            currentState.copy(
                cartListState = CartListUiState.Error(throwable.toUserMessage()),
            )
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
        withContentState { contentState ->
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

    private suspend fun calculateTotals() {
        val totalCount = cartRepository.count().getOrDefault(0)
        if (totalCount == 0) {
            _uiState.update {
                it.copy(
                    totalPrice = 0,
                    totalSelectedCount = 0,
                    isAllSelected = false,
                )
            }
            return
        }

        val allCartItems =
            cartRepository
                .getCartPage(
                    page = 0,
                    size = totalCount,
                ).getOrNull()
                ?.items ?: emptyList()

        val productIds =
            allCartItems
                .map {
                    it.productId
                }.toSet()
        val productsById =
            productRepository
                .findAllByIds(productIds)
                .getOrDefault(emptyMap())

        val deselectedIds = _uiState.value.deselectedProductIds
        var totalPrice = 0
        var selectedCount = 0

        allCartItems.forEach { item ->
            val product = productsById[item.productId]
            if (item.productId !in deselectedIds) {
                totalPrice += (product?.price?.value ?: 0) * item.quantity
                selectedCount++
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                totalPrice = totalPrice,
                totalSelectedCount = selectedCount,
                isAllSelected = deselectedIds.isEmpty() && allCartItems.isNotEmpty(),
            )
        }
    }

    private inline fun withContentState(block: (CartListUiState.Content) -> Unit) {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return
        block(contentState)
    }

    private inline fun <T> withContentState(
        defaultValue: T,
        block: (CartListUiState.Content) -> T,
    ): T {
        val contentState = _uiState.value.cartListState as? CartListUiState.Content ?: return defaultValue
        return block(contentState)
    }
}

class CartViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CartViewModel(
            productRepository = ShoppingRepositoryProvider.productRepository,
            cartRepository = ShoppingRepositoryProvider.cartRepository,
            networkMonitor = ShoppingRepositoryProvider.networkMonitor,
        ) as T
}

private fun Throwable.toUserMessage(): String =
    when (this) {
        is RemoteException -> userMessage
        else -> "알 수 없는 오류가 발생했습니다."
    }
