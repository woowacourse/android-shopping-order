package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem

interface CartLocalDataSource {
    fun getCart(): Result<Cart>

    fun saveCart(items: List<CartItem>): Result<Unit>

    fun add(cartItem: CartItem): Result<Unit>

    fun delete(productId: Long): Result<Unit>

    fun find(productId: Long): Result<CartItem?>

    fun exist(productId: Long): Result<Boolean>

    fun clear()
}
