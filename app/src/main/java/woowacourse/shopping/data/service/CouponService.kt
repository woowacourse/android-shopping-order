package woowacourse.shopping.data.service

import retrofit2.http.POST
import woowacourse.shopping.data.dto.response.ResponseCouponDto

interface CouponService {
    @POST("/coupons")
    suspend fun getCoupons(): List<ResponseCouponDto>
}
