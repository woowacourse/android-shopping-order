package woowacourse.shopping.data.repository.coupon

import woowacourse.shopping.domain.coupon.Coupon

interface CouponRepository {
    suspend fun loadCoupons(): List<Coupon>
}
