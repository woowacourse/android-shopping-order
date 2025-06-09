package woowacourse.shopping.data.network.service

import retrofit2.http.GET
import woowacourse.shopping.data.network.response.coupon.CouponResponse

interface CouponService {
    @GET("/coupons")
    suspend fun getAll(): List<CouponResponse>
}
