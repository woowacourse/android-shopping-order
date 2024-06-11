package woowacourse.shopping.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.cart.CartOrderRequest

interface OrderApiService {
    @POST("/orders")
    suspend fun orderItems(
        @Body cartItemIds: CartOrderRequest,
    )
}
