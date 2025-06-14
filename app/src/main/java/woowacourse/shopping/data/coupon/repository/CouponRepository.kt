package woowacourse.shopping.data.coupon.repository

import woowacourse.shopping.domain.order.Coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>
}
