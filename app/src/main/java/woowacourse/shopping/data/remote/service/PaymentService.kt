package woowacourse.shopping.data.remote.service

import retrofit2.http.GET
import woowacourse.shopping.data.dto.CouponDto

interface PaymentService {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponDto>
}
