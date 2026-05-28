package woowacourse.shopping.data.datasource.coupon

import woowacourse.shopping.domain.coupon.Coupon

interface CouponDataSource {
    suspend fun getAllCoupons(): List<Coupon>
}
