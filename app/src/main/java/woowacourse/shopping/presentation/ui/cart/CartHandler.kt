package woowacourse.shopping.presentation.ui.cart

import woowacourse.shopping.presentation.ui.QuantityHandler

interface CartHandler : QuantityHandler {
    fun deleteCartItem(cartId: Long)

    fun selectCartItem(cartId: Long)

    fun selectAllCartItems(isChecked: Boolean)

    fun handleOrderState()

    fun addProductToCart(productId: Long)
}
