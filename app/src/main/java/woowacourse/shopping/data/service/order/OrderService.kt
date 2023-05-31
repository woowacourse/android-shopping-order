package woowacourse.shopping.data.service.order

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.dto.OrderRequest

interface OrderService {
    @POST("/orders")
    fun order(@Body orderRequest: OrderRequest): Call<Unit>
}
