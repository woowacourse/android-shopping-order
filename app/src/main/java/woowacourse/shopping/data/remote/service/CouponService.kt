package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.remote.dto.response.Coupon

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): Response<List<Coupon>>
}
