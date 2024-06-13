package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupon

interface CouponRepository {
    suspend fun findCoupons(selectedCartIds: List<Long>): Result<List<Coupon>>
}
