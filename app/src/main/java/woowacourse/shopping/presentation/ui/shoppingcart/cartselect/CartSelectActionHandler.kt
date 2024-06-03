package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

interface CartSelectActionHandler {
    fun deleteCartProduct(cartProduct: CartProduct)

    fun checkCartProduct(cartProduct: CartProduct)

    fun checkAllCartProduct()
}
