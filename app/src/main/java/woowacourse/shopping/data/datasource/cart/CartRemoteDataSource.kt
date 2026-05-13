package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.remote.dto.CartItemDto

interface CartRemoteDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): List<CartItemDto>

    suspend fun getCartItemsCount(): Int

    suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    )

    suspend fun deleteCartItem(id: Int)

    suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    )
}
