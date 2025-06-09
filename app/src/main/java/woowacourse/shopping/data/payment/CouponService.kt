package woowacourse.shopping.data.payment

import retrofit2.http.GET

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): CouponResponse
}
