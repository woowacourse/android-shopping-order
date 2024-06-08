package woowacourse.shopping.remote.api

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.remote.dto.response.CouponResponse

interface CouponApiService {
    @GET("/coupons")
    suspend fun requestCoupons(): Response<List<CouponResponse>>
}
