package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.cart.CartOrderRequest

interface OrderApiService {
    @POST("/orders")
    fun orderItems(
        @Body cartItemIds: CartOrderRequest,
    ): Call<Unit>
}
