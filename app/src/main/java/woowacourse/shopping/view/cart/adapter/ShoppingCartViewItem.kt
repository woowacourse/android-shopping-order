package woowacourse.shopping.view.cart.adapter

import woowacourse.shopping.data.model.CartItem

sealed class ShoppingCartViewItem(open val viewType: Int) {
    data class CartViewItem(
        val cartItem: CartItem,
        val isChecked: Boolean = false,
        override val viewType: Int = CART_VIEW_TYPE,
    ) :
        ShoppingCartViewItem(viewType) {
        fun check(): CartViewItem {
            return this.copy(isChecked = !isChecked)
        }
    }

    data class CartPlaceHolderViewItem(override val viewType: Int = CART_PLACEHOLDER_VIEW_TYPE) :
        ShoppingCartViewItem(viewType)

    companion object {
        const val CART_PLACEHOLDER_VIEW_TYPE = 0
        const val CART_VIEW_TYPE = 1
    }
}
