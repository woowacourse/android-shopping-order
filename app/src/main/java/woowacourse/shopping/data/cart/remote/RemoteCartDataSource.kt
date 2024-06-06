package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.data.dto.request.CartSaveRequest
import woowacourse.shopping.data.dto.request.CartUpdateRequest
import woowacourse.shopping.data.dto.response.CartQuantityResponse
import woowacourse.shopping.data.dto.response.CartResponse
import woowacourse.shopping.data.remote.ApiClient

class RemoteCartDataSource {
    private val cartApiService: CartApiService =
        ApiClient.getApiClient().create(CartApiService::class.java)

    suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): CartResponse {
        return cartApiService.requestCartItems(page = startPage, size = pageSize)
    }

    suspend fun save(
        productId: Long,
        quantity: Int,
    ) {
        return cartApiService.requestAddCartItems(
            cartRequest =
                CartSaveRequest(
                    productId = productId,
                    quantity = quantity,
                ),
        )
    }

    suspend fun update(
        cartId: Long,
        quantity: Int,
    ) {
        return cartApiService.requestUpdateCartItems(
            cartId = cartId,
            request = CartUpdateRequest(quantity),
        )
    }

    suspend fun delete(cartId: Long) {
        return cartApiService.requestDeleteCartItems(cartId = cartId)
    }

    suspend fun getCount(): CartQuantityResponse {
        return cartApiService.requestGetCartItemsCount()
    }
}
