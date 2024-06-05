package woowacourse.shopping.data.cart.remote

import retrofit2.Call
import woowacourse.shopping.data.dto.request.CartSaveRequest
import woowacourse.shopping.data.dto.request.CartUpdateRequest
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse
import woowacourse.shopping.data.remote.ApiClient

class RemoteCartDataSource {
    private val cartApiService: CartApiService =
        ApiClient.getApiClient().create(CartApiService::class.java)

    fun load(
        startPage: Int,
        pageSize: Int,
    ): Call<CartResponse> {
        return cartApiService.requestCartItems(page = startPage, size = pageSize)
    }

    fun save(
        productId: Long,
        quantity: Int,
    ): Call<Unit> {
        return cartApiService.requestAddCartItems(
            cartRequest =
                CartSaveRequest(
                    productId = productId,
                    quantity = quantity,
                ),
        )
    }

    fun update(
        cartId: Long,
        quantity: Int,
    ): Call<Unit> {
        return cartApiService.requestUpdateCartItems(
            cartId = cartId.toInt(),
            request = CartUpdateRequest(quantity),
        )
    }

    fun delete(cartId: Long): Call<Unit> {
        return cartApiService.requestDeleteCartItems(
            cartId = cartId.toInt(),
        )
    }

    fun getCount(): Call<CartQuantityResponse> {
        return cartApiService.requestGetCartItemsCount()
    }
}
