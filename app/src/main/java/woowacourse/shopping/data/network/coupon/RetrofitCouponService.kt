package woowacourse.shopping.data.network.coupon

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.network.coupon.dto.CouponDto

interface RetrofitCouponService {
    @GET("/coupons")
    suspend fun requestCoupons(
        @Header("accept")
        accept: String = "*/*",
    ): Response<List<CouponDto>>
}
