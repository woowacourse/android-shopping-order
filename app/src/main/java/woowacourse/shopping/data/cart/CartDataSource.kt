package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.dto.CartProduct

interface CartDataSource {
    fun insertCartProduct(productId: Long, quantity: Int, callback: (cartId: Long) -> Unit)
    fun deleteCartProduct(cartId: Long, callback: () -> Unit)
    fun updateCartProduct(cartId: Long, quantity: Int, callback: () -> Unit)
    fun getAllCartProducts(callback: (List<CartProduct>) -> Unit)
}
