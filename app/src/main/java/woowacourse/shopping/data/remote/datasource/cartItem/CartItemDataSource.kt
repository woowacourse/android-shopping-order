package woowacourse.shopping.data.remote.datasource.cartItem

import retrofit2.Response
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.dto.response.CartsResponse
import woowacourse.shopping.data.remote.dto.response.QuantityResponse

interface CartItemDataSource {

    suspend fun getCartItems(
        page: Int = 0,
        size: Int = 20,
    ): Response<CartsResponse>

    suspend fun postCartItem(cartItemRequest: CartItemRequest): Response<Unit>

    suspend fun deleteCartItem(id: Int): Response<Unit>

    suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Response<Unit>

    suspend fun getCartItemsCounts(): Response<QuantityResponse>
}