package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.CouponState

interface CouponRepository {
    suspend fun getCouponStates(): Result<List<CouponState>>
}
