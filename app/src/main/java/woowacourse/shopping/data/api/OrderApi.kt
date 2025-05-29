package woowacourse.shopping.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.model.request.OrderProductsRequest

interface OrderApi {
    @POST("/orders")
    fun postOrderProducts(
        @Body orderProductsRequest: OrderProductsRequest,
    ): Call<Unit>
}
