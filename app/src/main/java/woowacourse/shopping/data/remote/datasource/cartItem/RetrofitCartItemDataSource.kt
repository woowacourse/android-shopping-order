package woowacourse.shopping.data.remote.datasource.cartItem

import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartsResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.utils.toIdOrNull

class RetrofitCartItemDataSource(
    private val cartItemApi: CartItemApi = CartItemApi.service(),
) : CartItemDataSource {
    override suspend fun getAllByPaging(
        page: Int,
        size: Int,
    ): Result<Message<CartsResponse>> =
        runCatching {
            val response = cartItemApi.getCartItems(page = page, size = size)
            Message(
                code = response.code(),
                body = response.body(),
            )
        }

    override suspend fun post(cartItemRequest: CartItemRequest): Result<Message<Int>> =
        runCatching {
            val response = cartItemApi.postCartItem(cartItemRequest = cartItemRequest)
            Message(
                code = response.code(),
                body = response.toIdOrNull(),
            )
        }

    override suspend fun delete(id: Int): Result<Message<Unit>> =
        runCatching {
            val response = cartItemApi.deleteCartItem(id = id)
            Message(
                code = response.code(),
                body = null,
            )
        }

    override suspend fun patch(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Message<Unit>> =
        runCatching {
            val response = cartItemApi.patchCartItem(id = id, quantityRequestDto = quantityRequestDto)
            Message(
                code = response.code(),
                body = null,
            )
        }

    override suspend fun getCount(): Result<Message<QuantityResponse>> =
        runCatching {
            val response = cartItemApi.getCartItemsCounts()
            Message(
                code = response.code(),
                body = response.body(),
            )
        }
}
