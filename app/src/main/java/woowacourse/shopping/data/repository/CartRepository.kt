package woowacourse.shopping.data.repository

import woowacourse.shopping.model.CartItem

interface CartRepository {
    suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): List<CartItem>

    suspend fun setCartItem(
        productId: String,
        quantity: Int,
    )

    suspend fun deleteItem(cartItemId: String)

    suspend fun getCartItemQuantity(cartItemId: String): Int?

    suspend fun getTotalCartItemQuantity(): Int
}
