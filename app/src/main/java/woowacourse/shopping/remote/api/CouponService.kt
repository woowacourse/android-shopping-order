package woowacourse.shopping.remote.api

import retrofit2.http.GET
import woowacourse.shopping.remote.model.response.CouponResponse

interface CouponService {
    @GET(COUPON_RELATIVE_URL)
    suspend fun getCoupons(): List<CouponResponse>

    companion object {
        private const val COUPON_RELATIVE_URL = "/coupons"
    }
}
