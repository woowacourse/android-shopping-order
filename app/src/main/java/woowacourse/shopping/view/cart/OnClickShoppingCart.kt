package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.model.CartItem

interface OnClickShoppingCart {
    fun clickBack()

    fun clickCartItem(productId: Long)

    fun clickRemoveCartItem(cartItem: CartItem)

    fun clickCheckBox(cartItem: CartItem)

    fun clickOrder()

    fun clickCheckAll()
}
