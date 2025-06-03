package woowacourse.shopping.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.model.order.OrderRequest

interface OrderService {
    @POST("/orders")
    fun addOrder(
        @Header("Authorization") key: String,
        @Body cartItemIds: OrderRequest,
    ): Call<Unit>
}
