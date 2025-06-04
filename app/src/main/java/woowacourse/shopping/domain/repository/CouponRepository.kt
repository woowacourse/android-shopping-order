package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupons

interface CouponRepository {
    suspend fun fetchAllCoupons(): Result<Coupons>
}
