package woowacourse.shopping.data.network.coupon

import woowacourse.shopping.domain.coupon.Coupon

interface CouponDao {
    suspend fun getCoupons(): List<Coupon>
}
