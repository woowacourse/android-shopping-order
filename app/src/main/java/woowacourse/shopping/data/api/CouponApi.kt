package woowacourse.shopping.data.api

import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.model.response.CouponsResponse

interface CouponApi {
    @GET("/coupons")
    suspend fun getAllCoupons(): Response<CouponsResponse>
}
