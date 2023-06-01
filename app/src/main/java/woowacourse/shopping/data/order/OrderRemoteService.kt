package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OrderRemoteService {

    @Headers("Content-Type: application/json")
    @POST("orders")
    fun requestOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequestBody: OrderRequestBody
    ): Call<Unit>
}

data class OrderRequestBody(val cartItemIds: List<Long>)
