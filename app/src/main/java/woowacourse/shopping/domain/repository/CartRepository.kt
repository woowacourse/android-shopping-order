package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem

interface CartRepository {
    suspend fun fetchCart(): Result<Unit>

    suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<PageableItem<CartProduct>>

    suspend fun deleteCartProduct(cartId: Long): Result<Unit>

    suspend fun increaseQuantity(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit>

    suspend fun decreaseQuantity(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit>

    suspend fun fetchCartProductCount(): Result<Int>

    suspend fun findQuantityByProductId(productId: Long): Result<Int>

    suspend fun findCartProductByProductId(productId: Long): Result<CartProduct>

    suspend fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>>

    suspend fun getAllCartProducts(): Result<List<CartProduct>>
}
