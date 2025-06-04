package woowacourse.shopping.data.api

import retrofit2.http.GET
import woowacourse.shopping.data.model.response.CouponResponse

interface CouponApi {
    @GET("/coupons")
    suspend fun getCoupons(): List<CouponResponse>
}
