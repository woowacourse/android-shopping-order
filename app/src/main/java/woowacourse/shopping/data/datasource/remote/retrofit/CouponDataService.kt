package woowacourse.shopping.data.datasource.remote.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO

interface CouponDataService {

    @GET("coupons")
    fun getCoupons(): Call<List<CouponDTO>>

    @GET("coupons/discount")
    fun getCouponDiscount(
        @Query("origin-price") originPrice: Int,
        @Query("member-coupon-id") couponId: Long,
    ): Call<CouponDiscountPriceDTO>
}
