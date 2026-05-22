package woowacourse.shopping.repository.http.api

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.repository.http.dto.coupon.CouponResponseDto

interface CouponApiService {
    @GET("coupons")
    suspend fun getCoupons(): Response<List<CouponResponseDto>>
}
