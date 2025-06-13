package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.coupon.Coupon

interface CouponDataSource {
    suspend fun fetchCoupons(): List<Coupon>
}
