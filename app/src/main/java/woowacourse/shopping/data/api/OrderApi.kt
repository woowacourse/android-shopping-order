package woowacourse.shopping.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.model.request.OrderProductsRequest

interface OrderApi {
    @POST("/orders")
    suspend fun postOrderProducts(
        @Body orderProductsRequest: OrderProductsRequest,
    ): Response<Unit>
}
