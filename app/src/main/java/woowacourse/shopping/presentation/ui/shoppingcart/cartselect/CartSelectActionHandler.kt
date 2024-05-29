package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

interface CartSelectActionHandler {
    fun deleteCartProduct(cartId: Int)

    fun checkCartProduct(cartId: Int)
}
