package woowacourse.shopping.data.respository.cart.source.local

import woowacouse.shopping.model.cart.CartProduct

interface CartLocalDataSource {
    fun insertCart(cartId: Long)
    fun deleteCart(cartId: Long)
    fun updateCartChecked(cartId: Long, isChecked: Boolean)
    fun selectAllCarts(): List<CartProduct>
}
