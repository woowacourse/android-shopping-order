package woowacourse.shopping.ui.order.recommend.action

import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem

interface RecommendShareActions {
    data class UpdateNewCartViewItems(val newCartViewItems: List<ShoppingCartViewItem.CartViewItem>) :
        RecommendShareActions

    data class PlusCartViewItemQuantity(val productId: Int) : RecommendShareActions

    data class MinusCartViewItemQuantity(val productId: Int) : RecommendShareActions
}
