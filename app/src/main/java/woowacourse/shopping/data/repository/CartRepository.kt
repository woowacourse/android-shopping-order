package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Money

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

    suspend fun getCartItemsCount(): Int

    suspend fun getTotalPrice(cartIds: List<String>): Money
}
