package woowacourse.shopping.ui.cart

data class CartUiState(
    val cartListState: CartListUiState = CartListUiState.Loading,
    val recommendedProducts: List<woowacourse.shopping.ui.shopping.ShoppingProductUiState> = emptyList(),
    val isRecommendedProductsLoading: Boolean = false,
    val recommendedProductFilterIds: Set<Long> = emptySet(),
    val pendingOrder: PendingOrderUiState = PendingOrderUiState(),
    val isOrdering: Boolean = false,
    val orderCompletedCount: Int = 0,
    val orderErrorMessage: String? = null,
    val isNetworkConnected: Boolean = true,
    val deselectedProductIds: Set<Long> = emptySet(),
)

data class PendingOrderUiState(
    val cartItemIds: List<Long> = emptyList(),
    val excludedProductIds: Set<Long> = emptySet(),
    val selectedCount: Int = 0,
    val totalPrice: Int = 0,
)

sealed interface CartListUiState {
    data object Loading : CartListUiState

    data class Content(
        val items: List<CartItemUiModel>,
        val currentPage: Int,
        val totalPages: Int,
        val hasPrevious: Boolean,
        val hasNext: Boolean,
    ) : CartListUiState

    data class Error(
        val message: String?,
    ) : CartListUiState
}
