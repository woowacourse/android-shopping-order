package woowacourse.shopping.ui.cart

import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product

data class CartUiState(
    val isLoading: Boolean = false,
    val pagedItems: List<CartItem> = emptyList(),
    val currentPage: Int = 1,
    val totalCartItemCount: Int = 0,
    val pageSize: Int = 5,
    val selectedItemIds: Set<Long> = emptySet(),
    val totalPrice: Long = 0,
    val isAllSelected: Boolean = false,
    val totalSelectedCount: Int = 0,
    val isCartScreen: Boolean = true,
    val recommendItems: List<Product> = emptyList()
) {
    val totalPages: Int get() = (totalCartItemCount - 1) / pageSize + 1
    val showPagination: Boolean get() = totalCartItemCount > pageSize
}
