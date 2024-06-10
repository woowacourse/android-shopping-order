package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.CouponState

interface CouponRepository {
    suspend fun getCoupons(): Result<List<CouponState>>
}
