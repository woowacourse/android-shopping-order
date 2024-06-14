package woowacourse.shopping.view.cart.list

import woowacourse.shopping.domain.model.CartItemDomain

sealed class ShoppingCartViewItem(open val viewType: Int) {
    data class CartViewItem(
        val cartItem: CartItemDomain,
        val isSelected: Boolean = false,
        override val viewType: Int = CART_VIEW_TYPE,
    ) : ShoppingCartViewItem(viewType)

    class CartPlaceHolderViewItem(override val viewType: Int = CART_PLACEHOLDER_VIEW_TYPE) :
        ShoppingCartViewItem(viewType)

    companion object {
        const val CART_PLACEHOLDER_VIEW_TYPE = 0
        const val CART_VIEW_TYPE = 1
    }
}
