package woowacourse.shopping.data.remote.api

import retrofit2.http.GET
import woowacourse.shopping.data.remote.dto.response.coupon.CouponResponse

interface CouponApi {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponResponse>
}