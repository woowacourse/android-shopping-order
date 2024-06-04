package woowacourse.shopping.data.remote

import retrofit2.Call
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse

class RemoteCartDataSource(
    private val cartService: CartService,
) : CartDataSource {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Call<CartResponse> {
        return cartService.getCartItems(page, size, sort)
    }

    override fun addCartItem(cartItemRequest: CartItemRequest): Call<Unit> {
        return cartService.addCartItem(cartItemRequest)
    }

    override fun deleteCartItem(productId: Int): Call<Unit> {
        return cartService.deleteCartItem(productId)
    }

    override fun updateCartItem(
        productId: Int,
        cartQuantityDto: CartQuantityDto,
    ): Call<Unit> {
        return cartService.updateCartItem(productId, cartQuantityDto)
    }

    override fun getCartTotalQuantity(): Call<CartQuantityDto> {
        return cartService.getCartTotalQuantity()
    }
}
