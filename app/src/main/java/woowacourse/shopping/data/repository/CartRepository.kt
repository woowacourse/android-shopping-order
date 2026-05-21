package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Money

interface CartRepository {
    suspend fun getCartItemsByPage(
        page: Int,
        size: Int,
    ): CartResponseResult

    suspend fun setCartItem(
        productId: Long,
        quantity: Int,
    )

    suspend fun deleteItem(cartItemId: Long)

    suspend fun getCartItemQuantity(productId: Long): Int?

    suspend fun getTotalCartItemQuantity(): Int

    suspend fun getCartItemsCount(): Int

    suspend fun getTotalPrice(cartIds: List<Long>): Money
}
