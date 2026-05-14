package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.Cart
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product

interface CartRepository {
    suspend fun getAllCartItems(): Cart

    suspend fun add(
        item: Product,
        quantity: Int,
    )

    suspend fun increase(item: Product)

    suspend fun decrease(item: Product)

    suspend fun delete(item: Product)

    suspend fun getPagedItems(
        page: Int,
        count: Int,
    ): List<CartItem>

    suspend fun getSize(): Int

    suspend fun getCartCount(): Int
}
