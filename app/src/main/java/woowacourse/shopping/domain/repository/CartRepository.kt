package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.cart.Quantity
import woowacourse.shopping.domain.product.Product

interface CartRepository {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartItems

    suspend fun getCartItemsCount(): Int

    suspend fun getAllCartItems(): CartItems

    suspend fun addProduct(
        product: Product,
        quantity: Quantity = Quantity.ONE,
    )

    suspend fun increase(productId: Int)

    suspend fun decrease(productId: Int)

    suspend fun remove(productId: Int)
}
