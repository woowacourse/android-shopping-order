package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

interface ShoppingCartRepository {
    suspend fun load(
        page: Int,
        size: Int,
    ): Result<ShoppingCarts>

    suspend fun add(
        product: Product,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateQuantity(
        shoppingCartId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun remove(shoppingCartId: Long): Result<Unit>

    suspend fun fetchAllQuantity(): Result<Int>
}
