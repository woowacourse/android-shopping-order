package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.coupon.Coupons

interface CouponRepository {
    suspend fun getCoupons(): Coupons
}
