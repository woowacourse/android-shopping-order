package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponRepository {
    suspend fun getCoupons(): List<Coupon>
}
