package woowacourse.shopping.data.shoppingCart.repository

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

interface ShoppingCartRepository {
    suspend fun load(
        page: Int = 0,
        size: Int = Integer.MAX_VALUE,
    ): Result<ShoppingCarts>

    suspend fun add(
        product: Product,
        quantity: Int,
    ): Result<ShoppingCartProduct>

    suspend fun updateQuantity(
        shoppingCartId: Long,
        quantity: Int,
    ): Result<ShoppingCartProduct?>

    suspend fun remove(shoppingCartId: Long): Result<Unit>

    suspend fun fetchAllQuantity(): Result<Int>
}
