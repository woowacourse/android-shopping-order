package woowacourse.shopping.ui.cart

import woowacourse.shopping.model.CartItem
import woowacourse.shopping.ui.common.model.LoadState
import woowacourse.shopping.ui.common.model.ProductUiModel

data class CartUiState(
    val loadState: LoadState = LoadState.Loading,
    val pagedItems: List<CartItem> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalCartItemCount: Int = 0,
    val selectedItemIds: Set<Long> = emptySet(),
    val totalSelectedPrice: Long = 0,
    val totalSelectedCount: Int = 0,
    val isAllSelected: Boolean = false,
    val isCartScreen: Boolean = true,
    val recommendItems: List<ProductUiModel> = emptyList(),
    val pageSize: Int = 5,
) {
    val showPagination: Boolean get() = totalCartItemCount > pageSize
}
