package woowacourse.shopping.remote.api

import retrofit2.http.GET
import woowacourse.shopping.remote.model.response.CouponResponse

interface CouponService {
    @GET(ApiClient.Coupon.GET_COUPONS)
    suspend fun getCoupons(): List<CouponResponse>
}
