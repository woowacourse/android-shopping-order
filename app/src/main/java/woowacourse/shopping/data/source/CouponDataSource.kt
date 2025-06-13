package woowacourse.shopping.data.source

import woowacourse.shopping.data.dto.coupon.Coupon

interface CouponDataSource {
    suspend fun getCoupons(): List<Coupon>
}
