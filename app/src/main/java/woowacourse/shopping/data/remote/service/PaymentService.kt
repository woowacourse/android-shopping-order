package woowacourse.shopping.data.remote.service

import retrofit2.http.GET
import woowacourse.shopping.data.remote.dto.CouponResponse

interface PaymentService {
    @GET("coupons")
    suspend fun getCoupons(): List<CouponResponse>
}
