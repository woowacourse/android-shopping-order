package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.UpdatedQuantity

interface CartRepository {
    suspend fun fetchCartItemsInfo(): Result<List<CartItem>>

    suspend fun fetchCartItemsInfoWithPage(
        page: Int,
        pageSize: Int,
    ): Result<List<CartItem>>

    suspend fun fetchTotalQuantity(): Result<Int>

    suspend fun findCartItemWithProductId(productId: Long): CartItem?

    suspend fun fetchItemQuantityWithProductId(productId: Long): Int

    suspend fun fetchCartItem(cartItemId: Long): CartItem?

    suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateCartItemQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(cartItemId: Long): Result<Unit>

    suspend fun deleteCartItemWithProductId(productId: Long): Result<Unit>

    suspend fun deleteAllItems(): Result<Unit>

    suspend fun makeOrder(order: Order): Result<Unit>

    suspend fun getCartItemsQuantities(productIds: Set<Long>): Result<List<UpdatedQuantity>>
}
