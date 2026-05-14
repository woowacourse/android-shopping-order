package woowacourse.shopping.ui.cart.list

data class CartUiState(
    val cartListState: CartListUiState = CartListUiState.Loading,
    val isNetworkConnected: Boolean = true,
    val deselectedProductIds: Set<Long> = emptySet(),
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
