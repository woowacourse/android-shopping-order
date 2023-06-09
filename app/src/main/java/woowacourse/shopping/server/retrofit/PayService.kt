package woowacourse.shopping.server.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import woowacourse.shopping.model.data.dto.OrderIdDTO
import woowacourse.shopping.model.data.dto.OrderPayDTO

interface PayService {

    @Headers("Authorization: Basic cmluZ2xvQGVtYWlsLmNvbTpyaW5nbG8xMDEwMjM1")
    @POST("/pay")
    fun postPay(
        @Body orderPay: OrderPayDTO
    ): Call<OrderIdDTO>
}
