package woowacourse.shopping.data.shoppingCart.datasource

import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto

interface ShoppingCartRemoteDataSource {
    suspend fun getCartCounts(): CartCountsResponseDto

    suspend fun saveCartItem(cartItemRequestDto: CartItemRequestDto)

    suspend fun updateCartItemQuantity(
        shoppingCartId: Long,
        cartItemQuantityRequestDto: CartItemQuantityRequestDto,
    )

    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ShoppingCartItemsResponseDto

    suspend fun deleteCartItem(shoppingCartId: Long)
}
