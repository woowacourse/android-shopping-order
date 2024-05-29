package woowacourse.shopping.view.cart.adapter

import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.domain.model.CartItem

sealed class ShoppingCartViewItem(open val viewType: Int) {
    data class CartViewItem(val cartItem: CartItem2, override val viewType: Int = CART_VIEW_TYPE) :
        ShoppingCartViewItem(viewType)

    data class CartPlaceHolderViewItem(override val viewType: Int = CART_PLACEHOLDER_VIEW_TYPE) :
        ShoppingCartViewItem(viewType)

    companion object {
        const val CART_PLACEHOLDER_VIEW_TYPE = 0
        const val CART_VIEW_TYPE = 1
    }
}
