package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.util.LoadState

data class CartUiState(
    val cartItems: List<CartItemUiModel> = emptyList(),
    val currentPage: Int = 1,
    val hasPreviousPage: Boolean = false,
    val hasNextPage: Boolean = false,
    val isAllSelected: Boolean = false,
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
    val canOrder: Boolean = false,
    val showPageNavigator: Boolean = false,
    val loadState: LoadState = LoadState.Initial,
)
