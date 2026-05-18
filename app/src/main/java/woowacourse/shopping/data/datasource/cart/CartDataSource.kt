package woowacourse.shopping.data.datasource.cart

import woowacourse.shopping.data.remote.dto.CartResponseDto
import woowacourse.shopping.domain.model.cart.Quantity

interface CartDataSource {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartResponseDto

    suspend fun getCartItemsCount(): Int

    suspend fun addCartItem(
        productId: Int,
        quantity: Quantity,
    )

    suspend fun deleteCartItem(id: Int)

    suspend fun updateCartItem(
        id: Int,
        quantity: Quantity,
    )

    suspend fun order(cartItemIds: List<Int>)
}
