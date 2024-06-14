package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.model.CartItem

interface OnClickShoppingCart {
    fun clickRemoveCartItem(cartItem: CartItem)

    fun clickCheckBox(cartItem: CartItem)

    fun clickToggleAll()

    fun clickOrder()
}
