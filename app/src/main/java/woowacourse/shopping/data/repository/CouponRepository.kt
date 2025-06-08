package woowacourse.shopping.data.repository

import woowacourse.shopping.domain.Coupon

interface CouponRepository {
    suspend fun loadCoupons(): List<Coupon>
}
