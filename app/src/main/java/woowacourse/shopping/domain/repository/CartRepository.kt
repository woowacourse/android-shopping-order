package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem

interface CartRepository {
    suspend fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<CartItem>>

    suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Int>

    suspend fun deleteCartItem(cartItemId: Int): Result<Unit>

    suspend fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartTotalQuantity(): Result<Int>
}
