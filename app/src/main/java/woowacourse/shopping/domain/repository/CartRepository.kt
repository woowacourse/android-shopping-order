package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem

interface CartRepository {
    suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableItem<CartProduct>>

    fun fetchAllCartItems(): Result<List<CartProduct>>

    fun findCartIdByProductId(productId: Long): Result<Long>

    suspend fun deleteCartItem(cartId: Long): Result<Unit>

    suspend fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit>

    suspend fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit>

    suspend fun fetchCartItemCount(): Result<Int>

    fun findQuantityByProductId(productId: Long): Result<Int>

    fun findCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>>
}
