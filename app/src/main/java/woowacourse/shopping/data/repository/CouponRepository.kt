package woowacourse.shopping.data.repository

import woowacourse.shopping.model.coupon.Coupon

interface CouponRepository {
    suspend fun getCoupons(): List<Coupon>
}
