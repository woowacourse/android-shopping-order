package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.coupon.Coupon

interface CouponRepository {
    suspend fun getAll(): Result<List<Coupon>>
}
