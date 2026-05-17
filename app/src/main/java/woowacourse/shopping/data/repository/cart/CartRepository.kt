package woowacourse.shopping.data.repository.cart

import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.Product

interface CartRepository {
    suspend fun loadCart(): Cart

    suspend fun loadTotalQuantity(): Int

    suspend fun pagination(
        page: Int,
        pageSize: Int,
    ): List<CartContent>

    suspend fun increase(
        product: Product,
        quantity: Int = 1,
    )

    suspend fun decrease(productId: Long)

    suspend fun remove(productId: Long)

    suspend fun setProductQuantity(
        product: Product,
        quantity: Int,
    )
}
