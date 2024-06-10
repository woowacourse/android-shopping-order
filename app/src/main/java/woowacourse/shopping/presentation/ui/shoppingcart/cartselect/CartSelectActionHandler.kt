package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

interface CartSelectActionHandler {
    fun deleteCartProduct(cartProduct: CartProduct)

    fun toggleCartProduct(cartProduct: CartProduct)

    fun checkAllCartProduct()
}
