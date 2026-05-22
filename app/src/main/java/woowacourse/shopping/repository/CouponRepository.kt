package woowacourse.shopping.repository

import woowacourse.shopping.model.coupon.Coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>
}
