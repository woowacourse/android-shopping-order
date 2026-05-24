package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.http.GET
import woowacourse.shopping.data.remote.retrofit.dto.CouponItem

interface CouponRetrofitInterface {
    @GET("/coupons")
    suspend fun requestCoupons(): List<CouponItem>
}
