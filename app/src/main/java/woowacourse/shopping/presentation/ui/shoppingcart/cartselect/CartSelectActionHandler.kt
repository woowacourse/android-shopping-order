package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

interface CartSelectActionHandler {
    fun deleteCartProduct(cartProduct: CartProduct)

    fun updateCheckState(cartProduct: CartProduct)

    fun checkAllCartProduct()
}
