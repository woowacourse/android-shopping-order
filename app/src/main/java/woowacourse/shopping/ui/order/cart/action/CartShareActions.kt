package woowacourse.shopping.ui.order.cart.action

import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem

interface CartShareActions {
    data class UpdateNewCartViewItems(val newCartViewItems: List<ShoppingCartViewItem.CartViewItem>) : CartShareActions

    data class CheckCartViewItem(val cartItemId: Int) : CartShareActions

    data class DeleteCartViewItem(val cartItemId: Int) : CartShareActions

    data class PlusCartViewItemQuantity(val productId: Int) : CartShareActions

    data class MinusCartViewItemQuantity(val productId: Int) : CartShareActions
}
