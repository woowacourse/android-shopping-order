package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.Cart
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.PageResult
import woowacourse.shopping.data.model.Product

interface CartRepository {
    suspend fun getAllCartItems(): Cart

    suspend fun add(
        item: Product,
        quantity: Int = 1,
    )

    suspend fun decrease(item: Product)

    suspend fun delete(item: Product)

    suspend fun getCartPage(
        page: Int,
        count: Int,
    ): PageResult<CartItem>

    suspend fun getCartCount(): Int

    suspend fun findCartItem(id: Long): CartItem?

    suspend fun deleteCartItem(id: Long)
}
