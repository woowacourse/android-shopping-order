package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

interface OrderRetrofitInterface {
    /**
     * @Header("Accept") accept: String = 가 없으면 어떻게 될까요?
     */
    @POST("/orders")
    fun order(
        @Header("Accept") accept: String = "*/*",
        @Body order: OrderInfo,
    ): Call<Void>
}
