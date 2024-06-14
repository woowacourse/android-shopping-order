package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.dto.request.OrderRequest

interface OrderApi {
    @POST("/orders")
    suspend fun postOrders(
        @Header("accept") accept: String = "*/*",
        @Body orderRequest: OrderRequest,
    ): Response<Unit>

    companion object {
        private var service: OrderApi? = null

        fun service(): OrderApi {
            return service ?: RetrofitModule.defaultBuild.create(OrderApi::class.java)
        }
    }
}
