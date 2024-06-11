package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.model.cart.CartItem

interface OnClickShoppingCart {
    fun clickRemoveCartItem(cartItem: CartItem)

    fun clickCheckBox(cartItem: CartItem)

    fun clickCheckAll()

    fun clickOrder()
}
