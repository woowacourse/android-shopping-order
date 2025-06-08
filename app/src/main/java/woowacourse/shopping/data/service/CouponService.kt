package woowacourse.shopping.data.service

import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.model.response.coupon.CouponResponse

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(
        @Header("accept") accept: String = "*/*",
    ): List<CouponResponse>
}
