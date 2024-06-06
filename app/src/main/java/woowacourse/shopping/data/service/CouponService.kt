package woowacourse.shopping.data.service

import retrofit2.http.GET
import woowacourse.shopping.data.dto.response.ResponseCouponDto

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): List<ResponseCouponDto>
}
