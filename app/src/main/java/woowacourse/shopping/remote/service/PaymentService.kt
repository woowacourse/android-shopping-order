package woowacourse.shopping.remote.service

import retrofit2.http.GET
import woowacourse.shopping.remote.dto.CouponDto

interface PaymentService {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponDto>
}
