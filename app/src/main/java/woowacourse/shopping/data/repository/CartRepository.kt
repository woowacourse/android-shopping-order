package woowacourse.shopping.data.repository

interface CartRepository {
    suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult

    suspend fun setCartItem(
        productId: String,
        quantity: Int,
    )

    suspend fun deleteItem(cartItemId: String)

    suspend fun getCartItemQuantity(cartItemId: String): Int?

    suspend fun getTotalCartItemQuantity(): Int
}
