package woowacourse.shopping.data.remote.datasource.cartItem

import woowacourse.shopping.data.remote.dto.Message
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartsResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface CartItemDataSource {
    suspend fun getAllByPaging(
        page: Int = 0,
        size: Int = 20,
    ): Result<Message<CartsResponse>>

    suspend fun post(cartItemRequest: CartItemRequest): Result<Message<Int>>

    suspend fun delete(id: Int): Result<Message<Unit>>

    suspend fun patch(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Message<Unit>>

    suspend fun getCount(): Result<Message<QuantityResponse>>
}
