package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.dto.order.Coupons

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): Response<Coupons>
}
