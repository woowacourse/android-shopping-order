package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent

interface CartRepository {
    suspend fun loadCart(): Cart

    suspend fun loadTotalQuantity(): Int

    suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent>

    suspend fun insert(
        productId: String,
        quantity: Int = 1,
    )

    suspend fun updateQuantity(
        contentId: String,
        quantity: Int,
    )

    suspend fun decrease(contentId: String)

    suspend fun remove(contentId: String)
}
