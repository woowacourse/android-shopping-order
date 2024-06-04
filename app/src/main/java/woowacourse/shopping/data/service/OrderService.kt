package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.dto.request.RequestPostOrderDto

interface OrderService {

    @POST("/orders")
    fun postOrders(
        @Header("accept") accept: String = "*/*",
        @Body request: RequestPostOrderDto
    ): Call<Unit>

}
