package woowacourse.shopping.data.datasource.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO

interface CouponDataService {

    @GET("coupons")
    fun getCoupons(
        @Header("Authorization") token: String,
    ): Call<List<CouponDTO>>

    @GET("coupons/discount")
    fun getCouponDiscount(
        @Header("Authorization") token: String,
        @Query("origin-price") originPrice: Int,
        @Query("member-coupon-id") couponId: Long,
    ): Call<CouponDiscountPriceDTO>
}
