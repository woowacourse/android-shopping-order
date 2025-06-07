package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupon

interface CouponRepository {
    suspend fun fetchAllCoupons(): Result<List<Coupon>>
}
