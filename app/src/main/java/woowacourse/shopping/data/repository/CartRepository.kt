package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import woowacourse.shopping.model.CartItem

interface CartRepository {
    suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): Result<CartResponseResult>

    suspend fun setCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun addCartItemQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteItem(cartItemId: Long): Result<Unit>

    suspend fun getTotalCartItemQuantity(): Result<Int>

    fun getCartQuantityMap(): Flow<Map<Long, Int>>

    suspend fun getAllCartItems(): Result<List<CartItem>>
}
