package woowacourse.shopping.data.remote.source

import retrofit2.Response
import woowacourse.shopping.data.remote.api.CartApiService
import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemRequest
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.source.CartItemDataSource

class CartItemDataSourceImpl(
    private val cartApiService: CartApiService = NetworkManager.cartService(),
) : CartItemDataSource {
    override suspend fun loadCartItems(): Response<CartItemResponse> {
        return cartApiService.requestCartItems(
            page = DEFAULT_ITEM_OFFSET,
            size = MAX_CART_ITEM_SIZE,
        )
    }

    override suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Response<CartItemResponse> {
        return cartApiService.requestCartItems(
            page = page,
            size = size,
        )
    }

    override suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Response<Unit> {
        return cartApiService.insertCartItem(
            cartItemRequest =
                CartItemRequest(
                    productId = productId,
                    quantity = quantity,
                ),
        )
    }

    override suspend fun deleteCartItem(id: Long): Response<Unit> {
        return cartApiService.deleteCartItem(id = id)
    }

    override suspend fun updateCartItem(
        id: Long,
        quantity: Int,
    ): Response<Unit> {
        return cartApiService.updateCartItem(
            id = id,
            quantity = CartItemQuantityDto(quantity),
        )
    }

    override suspend fun loadCartItemCount(): Response<CartItemQuantityDto> {
        return cartApiService.requestCartItemCount()
    }

    companion object {
        private const val MAX_CART_ITEM_SIZE = 50
        private const val DEFAULT_ITEM_OFFSET = 0
    }
}
