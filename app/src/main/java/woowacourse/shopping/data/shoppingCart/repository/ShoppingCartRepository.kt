package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

interface ShoppingCartRepository {
    suspend fun load(
        page: Int,
        size: Int,
    ): ShoppingCarts

    suspend fun add(
        product: Product,
        quantity: Int,
    )

    suspend fun updateQuantity(
        shoppingCartId: Long,
        quantity: Int,
    )

    suspend fun remove(shoppingCartId: Long)

    suspend fun fetchAllQuantity(): Int
}
