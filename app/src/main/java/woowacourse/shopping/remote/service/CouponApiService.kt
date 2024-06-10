package woowacourse.shopping.remote.service

import retrofit2.http.GET
import woowacourse.shopping.remote.model.response.CouponResponse

interface CouponApiService {
    @GET("/coupons")
    suspend fun coupons(): List<CouponResponse>
}