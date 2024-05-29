package woowacourse.shopping.presentation.ui.shoppingcart

interface ShoppingCartActionHandler {
    fun deleteCartProduct(cartId: Int)

    fun checkCartProduct(cartId: Int)
}
