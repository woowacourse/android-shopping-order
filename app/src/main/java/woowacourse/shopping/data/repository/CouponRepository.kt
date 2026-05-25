package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.Coupon

interface CouponRepository {
    suspend fun getCoupons(): List<Coupon>
}
