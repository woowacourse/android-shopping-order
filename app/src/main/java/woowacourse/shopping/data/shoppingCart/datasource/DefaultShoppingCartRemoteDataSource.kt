package woowacourse.shopping.data.shoppingCart.datasource

import woowacourse.shopping.data.shoppingCart.remote.dto.CartCountsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemQuantityRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.CartItemRequestDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto
import woowacourse.shopping.data.shoppingCart.remote.service.ShoppingCartService

class DefaultShoppingCartRemoteDataSource(
    private val shoppingCartService: ShoppingCartService,
) : ShoppingCartRemoteDataSource {
    override suspend fun getCartCounts(): CartCountsResponseDto = shoppingCartService.getCartCounts()

    override suspend fun saveCartItem(cartItemRequestDto: CartItemRequestDto) {
        shoppingCartService.postCartItem(cartItemRequestDto)
    }

    override suspend fun updateCartItemQuantity(
        shoppingCartId: Long,
        cartItemQuantityRequestDto: CartItemQuantityRequestDto,
    ) {
        shoppingCartService.patchCartItem(
            shoppingCartId = shoppingCartId,
            cartItemQuantityRequestDto = cartItemQuantityRequestDto,
        )
    }

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ShoppingCartItemsResponseDto = shoppingCartService.getCartItems(page, size)

    override suspend fun deleteCartItem(shoppingCartId: Long) {
        shoppingCartService.deleteCartItem(shoppingCartId)
    }
}
