package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto

interface OrderService {

    @GET("/coupons")
    fun getCoupons(
        @Header("Authorization") token: String,
    ): Call<List<CouponsResponseDto>>

    @GET("/coupons/discount")
    fun getAppliedPrice(
        @Header("Authorization") token: String,
        @Query("origin-price") originPrice: Int,
        @Query("member-coupon-id") couponId: Int,
    ): Call<AppliedTotalResponseDto>
}
