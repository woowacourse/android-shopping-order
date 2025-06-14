package woowacourse.shopping.data.coupon.repository

import woowacourse.shopping.domain.coupon.Coupon

interface CouponRepository {
    suspend fun loadCoupons(): List<Coupon>
}