package woowacourse.shopping.data.coupon.remote

import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.dto.response.CouponDTO

interface CouponApiService {
    @GET("/coupons")
    suspend fun requestCoupons(
        @Header("accept") accept: String = "*/*",
    ): List<CouponDTO>
}
