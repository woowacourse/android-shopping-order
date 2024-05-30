package woowacourse.shopping.view.cart.adapter

sealed class ShoppingCartViewItem(open val viewType: Int) {
    data class CartViewItem(
        val cartItem: woowacourse.shopping.data.model.CartItem,
        val isSelected: Boolean = false,
        override val viewType: Int = CART_VIEW_TYPE,
    ) :
        ShoppingCartViewItem(viewType) {
        fun select(): CartViewItem {
            return this.copy(isSelected = !isSelected)
        }
    }

    data class CartPlaceHolderViewItem(override val viewType: Int = CART_PLACEHOLDER_VIEW_TYPE) :
        ShoppingCartViewItem(viewType)

    companion object {
        const val CART_PLACEHOLDER_VIEW_TYPE = 0
        const val CART_VIEW_TYPE = 1
    }
}
