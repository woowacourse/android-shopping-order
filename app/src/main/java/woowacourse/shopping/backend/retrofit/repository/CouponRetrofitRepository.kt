package woowacourse.shopping.backend.retrofit.repository

import woowacourse.shopping.backend.retrofit.api.CouponRetrofit
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.coupon.Coupon

class CouponRetrofitRepository(
    private val apiService: CouponRetrofit,
) {
    suspend fun getCoupons(): List<Coupon> =
        apiService.getCoupons().map { couponResponse ->
            couponResponse.toDomain()
        }
}
