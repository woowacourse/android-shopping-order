package woowacourse.shopping.ui.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.ui.cart.component.CartSelection
import woowacourse.shopping.ui.cart.component.paginate
import woowacourse.shopping.ui.model.mapper.toUiModel

fun CartUiState.toUiState(
    cartItems: List<CartItem>,
    selectedItems: ImmutableList<String>,
    page: Int,
    pageSize: Int,
): CartUiState {
    val cartPage = cartItems.paginate(page = page, pageSize = pageSize)
    val selection = CartSelection(selectedItems).filterSameIds(cartItems)

    return copy(
        items =
            cartPage.items
                .map { cartItem -> cartItem.toUiModel(selection.contains(cartItem.id)) }
                .toImmutableList(),
        page = cartPage.index,
        isCanMoveNext = cartPage.canMoveNext,
        totalCartCount = cartItems.size,
        totalCartQuantity = cartItems.sumOf { it.quantity },
        totalPrice = selection.totalPrice(cartItems).amount,
        isAllChecked = selection.isAllSelected(cartItems),
        selectedCartItems = selection.ids,
        selectedCartItemCount = selection.selectedCount,
    )
}
