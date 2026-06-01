package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo

interface OrderRetrofitInterface {
    @POST("/orders")
    suspend fun order(
        @Body orderInfo: OrderInfo,
    )
}
