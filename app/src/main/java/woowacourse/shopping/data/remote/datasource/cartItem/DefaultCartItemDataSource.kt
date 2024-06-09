package woowacourse.shopping.data.remote.datasource.cartItem

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartsResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.data.remote.service.CartItemApi

class DefaultCartItemDataSource(
    private val cartItemApi: CartItemApi = CartItemApi.service(),
) : CartItemDataSource {
    override suspend fun getAllByPaging(
        page: Int,
        size: Int,
    ): Response<CartsResponse> {
        return cartItemApi.getCartItems(page = page, size = size)
    }

    override suspend fun post(cartItemRequest: CartItemRequest): Response<Unit> {
        return cartItemApi.postCartItem(cartItemRequest = cartItemRequest)
    }

    override suspend fun delete(id: Int): Response<Unit> {
        return cartItemApi.deleteCartItem(id = id)
    }

    override suspend fun patch(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Response<Unit> {
        return cartItemApi.patchCartItem(id = id, quantityRequestDto = quantityRequestDto)
    }

    override suspend fun getCount(): Response<QuantityResponse> {
        return cartItemApi.getCartItemsCounts()
    }
}
