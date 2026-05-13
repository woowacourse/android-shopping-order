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

    suspend fun increase(product: Product)

    suspend fun decrease(productId: String)

    suspend fun remove(productId: String)

    suspend fun setProductQuantity(
        productId: String,
        quantity: Int,
    )
}
