package woowacourse.shopping.backend.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.backend.retrofit.dto.coupon.CouponResponse

interface CouponRetrofit {
    @GET("/coupons")
    suspend fun getCoupons(
        @Header("Accept") accept: String = "application/json",
    ): List<CouponResponse>
}
