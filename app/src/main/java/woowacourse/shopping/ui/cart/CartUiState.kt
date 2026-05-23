package woowacourse.shopping.ui.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.ProductUiModel

data class CartUiState(
    val items: ImmutableList<CartItemUiModel> = persistentListOf(),
    val recommendProducts: ImmutableList<ProductUiModel> = persistentListOf(),
    val pageState: PageState = PageState(),
    val cartSummary: CartSummaryState = CartSummaryState(),
    val uiInfoState: UiInfoState = UiInfoState(),
    val selectedCartState: SelectedCartState = SelectedCartState(),
)

data class PageState(
    val page: Int = 0,
    val isCanMoveNext: Boolean = false,
)

data class UiInfoState(
    val isLoading: Boolean = true,
    val isOrder: Boolean = false,
)

data class CartSummaryState(
    val totalCartQuantity: Int = 0,
    val totalCartCount: Int = 0,
    val totalPrice: Long = 0,
)

data class SelectedCartState(
    val selectedCartItems: ImmutableList<String> = persistentListOf(),
    val isAllChecked: Boolean = false,
) {
    val selectedCartItemCount: Int
        get() = selectedCartItems.size
}
