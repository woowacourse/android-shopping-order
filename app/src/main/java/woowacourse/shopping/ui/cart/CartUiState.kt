package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.productList.ProductUiModel
import woowacourse.shopping.ui.util.LoadState

data class CartUiState(
    val cartItems: List<CartItemUiModel> = emptyList(),
    val currentPage: Int = 1,
    val hasPreviousPage: Boolean = false,
    val hasNextPage: Boolean = false,
    val isAllSelected: Boolean = false,
    val totalPrice: Int = 0,
    val totalCount: Int = 0,
    val showPageNavigator: Boolean = false,
    val recommendProducts: List<ProductUiModel> = emptyList(),
    val loadState: LoadState = LoadState.Initial,
)

enum class CartFlow {
    CART,
    RECOMMEND,
}
