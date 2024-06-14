package woowacourse.shopping.remote.service

import retrofit2.Response
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.request.OrderRequest

interface OrderService {
    @POST("orders")
    suspend fun orderProducts(
        @Body body: OrderRequest,
    ): Response<Unit>

    companion object {
        private var instance: OrderService? = null

        fun instance(): OrderService =
            instance ?: synchronized(this) {
                instance ?: RetrofitModule.retrofit().create<OrderService>()
                    .also { instance = it }
            }
    }
}
