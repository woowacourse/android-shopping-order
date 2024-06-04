package woowacourse.shopping.remote.service

import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.request.OrderRequest

interface OrderService {
    @POST("orders")
    fun orderProducts(
        @Body body: OrderRequest,
    ): Call<Unit>

    companion object {
        private var instance: OrderService? = null

        fun instance(): OrderService =
            instance ?: synchronized(this) {
                instance ?: RetrofitModule.retrofit().create<OrderService>()
                    .also { instance = it }
            }
    }
}
