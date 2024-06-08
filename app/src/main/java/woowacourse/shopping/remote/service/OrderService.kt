package woowacourse.shopping.remote.service

import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.dto.response.CouponResponse

interface OrderService {
    @POST("orders")
    suspend fun orderProducts(
        @Body body: OrderRequest,
    )

    @GET("coupons")
    suspend fun loadDiscountCoupons(): List<CouponResponse>

    companion object {
        private var instance: OrderService? = null

        fun instance(): OrderService =
            instance ?: synchronized(this) {
                instance ?: RetrofitModule.retrofit().create<OrderService>().also {
                    instance = it
                }
            }
    }
}
