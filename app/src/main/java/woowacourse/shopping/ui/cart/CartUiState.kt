package woowacourse.shopping.ui.cart

sealed interface CartUiState {
    data object Loading : CartUiState

    data object Empty : CartUiState

    data class Success(
        val cartItems: List<CartItemUiModel>,
        val currentPage: Int,
        val totalPages: Int,
        val hasPrevious: Boolean,
        val hasNext: Boolean,
        val selectedItems: Set<Int> = emptySet(),
    ) : CartUiState {
        val showPageNavigator: Boolean
            get() = totalPages > 1
    }

    data class Error(
        val throwable: Throwable,
    ) : CartUiState
}
