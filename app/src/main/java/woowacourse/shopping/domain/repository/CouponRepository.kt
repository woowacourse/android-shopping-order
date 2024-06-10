package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Orders

interface CouponRepository {
    suspend fun availableCoupons(orders: Orders): Result<List<Coupon>>

    suspend fun discountAmount(couponId: Long, orders: Orders): Result<Int>
}
