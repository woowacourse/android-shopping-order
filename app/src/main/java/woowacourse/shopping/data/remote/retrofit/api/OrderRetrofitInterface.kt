package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

interface OrderRetrofitInterface {
    @POST("/orders")
    fun order(
        @Header("Accept") accept: String = "*/*",
        @Body order: OrderInfo,
    ): Call<Void>
}
