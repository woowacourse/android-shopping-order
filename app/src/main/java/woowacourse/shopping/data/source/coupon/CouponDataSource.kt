package woowacourse.shopping.data.source.coupon

import woowacourse.shopping.domain.coupon.Coupon

interface CouponDataSource {
    suspend fun loadCoupons(): List<Coupon>
}
