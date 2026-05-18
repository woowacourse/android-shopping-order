package woowacourse.shopping.ui.state

import woowacourse.shopping.model.ShoppingCartItem

data class ShoppingCartState(
    val items: List<ShoppingCartItem> = emptyList(),
    val selectedProductIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val selectedItemCount: Int = 0,
    val canOrder: Boolean = false,
    val canMoveToPreviousPage: Boolean = false,
    val canMoveToNextPage: Boolean = false,
) {
    val hasApiError: Boolean
        get() = errorMessage != null
}