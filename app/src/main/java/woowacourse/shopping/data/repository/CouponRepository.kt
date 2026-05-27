package woowacourse.shopping.data.repository

import woowacourse.shopping.model.Coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>
}
