package woowacourse.shopping.data.respository.cart.source.local

import woowacourse.shopping.data.model.CartLocalEntity

interface CartLocalDataSource {
    fun insertCart(cartId: Long)
    fun deleteCart(cartId: Long)
    fun updateCartChecked(cartId: Long, isChecked: Boolean)
    fun selectAllCarts(): List<CartLocalEntity>
}
