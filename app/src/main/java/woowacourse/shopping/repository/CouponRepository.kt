package woowacourse.shopping.repository

import woowacourse.shopping.model.Coupon

interface CouponRepository {
    suspend fun getCoupons(): List<Coupon>
}
