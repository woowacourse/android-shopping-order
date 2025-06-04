package woowacourse.shopping.data.network.service

import retrofit2.http.GET
import woowacourse.shopping.data.network.response.coupons.CouponsResponse

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponsResponse>
}
