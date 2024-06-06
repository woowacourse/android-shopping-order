package woowacourse.shopping.data.remote.service

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import retrofit2.Response
import retrofit2.http.GET
import woowacourse.shopping.data.remote.dto.response.BuyXGetYCoupon
import woowacourse.shopping.data.remote.dto.response.Coupon
import woowacourse.shopping.data.remote.dto.response.FixedDiscountCoupon
import woowacourse.shopping.data.remote.dto.response.FreeShippingCoupon
import woowacourse.shopping.data.remote.dto.response.PercentageDiscountCoupon

interface CouponService {
    @GET("/coupons")
    suspend fun getCoupons(): Response<List<Coupon>>
}
