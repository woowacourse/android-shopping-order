package woowacourse.shopping.data.remote.service

import retrofit2.http.GET
import retrofit2.http.Header
import woowacourse.shopping.data.remote.dto.response.Coupons

interface CouponApi {
    @GET("coupons")
    suspend fun getCoupons(
        @Header("accept") accept: String = "*/*",
    ): List<Coupons>
}
