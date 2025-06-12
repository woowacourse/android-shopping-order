package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PageableItem

interface CartRepository {
    suspend fun fetchCart()

    suspend fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableItem<CartProduct>>

    fun fetchAllCartItems(): Result<List<CartProduct>>

    fun fetchCartIdByProductId(productId: Long): Result<Long>

    fun fetchCartProductByProductId(productId: Long): Result<CartProduct>

    suspend fun deleteCartItem(cartId: Long): Result<Unit>

    suspend fun deleteCartItems(cartIds: List<Long>): Result<Unit>

    suspend fun insertCartProductQuantityToCart(
        productId: Long,
        increaseCount: Int,
    ): Result<Unit>

    suspend fun decreaseCartProductQuantityFromCart(
        productId: Long,
        decreaseCount: Int,
    ): Result<Unit>

    suspend fun fetchCartItemCount(): Result<Int>

    suspend fun hasCartItem(): Result<Boolean>

    fun fetchCartProductsByProductIds(productIds: List<Long>): Result<List<CartProduct>>

    fun fetchCartProductsByIds(cartIds: List<Long>): Result<List<CartProduct>>
}
