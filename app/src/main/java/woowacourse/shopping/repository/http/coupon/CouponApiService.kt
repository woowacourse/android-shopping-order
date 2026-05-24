package woowacourse.shopping.repository.http.coupon

import retrofit2.Response
import retrofit2.http.GET

interface CouponApiService {
    @GET("coupons")
    suspend fun getCoupons(): Response<List<CouponResponseDto>>
}
