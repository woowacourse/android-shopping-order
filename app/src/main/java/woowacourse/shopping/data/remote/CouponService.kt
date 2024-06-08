package woowacourse.shopping.data.remote

import retrofit2.http.GET
import woowacourse.shopping.data.model.coupon.CouponResponseItem

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponResponseItem>
}
