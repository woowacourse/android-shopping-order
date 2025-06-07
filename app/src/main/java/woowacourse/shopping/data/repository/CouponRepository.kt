package woowacourse.shopping.data.repository

import woowacourse.shopping.order.Coupon

interface CouponRepository {
    suspend fun getCoupons(): List<Coupon>
}
