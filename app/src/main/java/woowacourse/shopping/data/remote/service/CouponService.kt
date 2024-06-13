package woowacourse.shopping.data.remote.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.dto.response.ResponseCouponDto

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): Response<List<ResponseCouponDto>>
}
