package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem

interface CartLocalDataSource {
    fun getCart(): Cart

    fun saveCart(items: List<CartItem>)

    fun add(cartItem: CartItem)

    fun delete(productId: Long)

    fun find(productId: Long): CartItem?

    fun exist(productId: Long): Boolean

    fun clear()
}
