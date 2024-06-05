package woowacourse.shopping.ui.order.cart.adapter

import woowacourse.shopping.domain.model.CartItem

sealed class ShoppingCartViewItem(open val viewType: Int) {
    data class CartViewItem(
        val cartItem: CartItem,
        val isChecked: Boolean = false,
        override val viewType: Int = CART_VIEW_TYPE,
    ) : ShoppingCartViewItem(viewType) {
        fun toggleCheck(): CartViewItem {
            return this.copy(isChecked = !isChecked)
        }

        fun check(): CartViewItem {
            return this.copy(isChecked = true)
        }

        fun unCheck(): CartViewItem {
            return this.copy(isChecked = false)
        }
    }

    data class CartPlaceHolderViewItem(override val viewType: Int = CART_PLACEHOLDER_VIEW_TYPE) :
        ShoppingCartViewItem(viewType)

    companion object {
        const val CART_PLACEHOLDER_VIEW_TYPE = 0
        const val CART_VIEW_TYPE = 1
    }
}
