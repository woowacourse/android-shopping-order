package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.response.Quantity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.PagedCartItems

interface CartRepository {
    suspend fun getCartItems(
        page: Int,
        limit: Int,
    ): Result<PagedCartItems>

    suspend fun getAllCartItems(): Result<List<CartItem>>

    suspend fun getAllCartItemsCount(): Result<Quantity>

    suspend fun deleteCartItem(productId: Long): Result<Long>

    suspend fun upsertCartItemQuantity(
        productId: Long,
        cartId: Long? = null,
        quantity: Int,
    ): Result<Long>

    suspend fun addOrIncreaseCartItem(productId: Long): Result<Long>

    suspend fun addCartItem(cartItem: CartItem): Result<Long>

    suspend fun increaseCartItem(cartItem: CartItem): Result<Long>

    suspend fun decreaseCartItem(cartItem: CartItem): Result<Long>
}
