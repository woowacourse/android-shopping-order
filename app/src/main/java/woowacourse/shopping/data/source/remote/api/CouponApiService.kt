package woowacourse.shopping.data.source.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.model.CouponResponse

interface CouponApiService {
    @GET("/coupons")
    suspend fun getCoupons(
        @Header("accept") accept: String = "*/*",
    ): Response<List<CouponResponse>>
}