package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupons.Coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>
}
