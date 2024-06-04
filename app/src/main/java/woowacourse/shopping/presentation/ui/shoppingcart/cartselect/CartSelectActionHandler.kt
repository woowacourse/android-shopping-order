package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import woowacourse.shopping.domain.model.Cart

interface CartSelectActionHandler {
    fun deleteCartProduct(cartId: Int)

    fun checkCartProduct(cart: Cart)

    fun checkAllCartProduct()
}
