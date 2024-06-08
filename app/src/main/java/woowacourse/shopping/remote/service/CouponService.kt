package woowacourse.shopping.remote.service

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.remote.dto.response.CouponsResponse

interface CouponService {
    @GET("/coupons")
    suspend fun loadCoupons(): Response<CouponsResponse>
}
