package woowacourse.shopping.data.remote.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto

interface OrderService {
    @POST("/orders")
    fun postOrders(
        @Header("accept") accept: String = "*/*",
        @Body request: RequestOrderPostDto,
    ): Call<Unit>
}
