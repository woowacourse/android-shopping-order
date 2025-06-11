package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Product

interface CouponRepository {
    suspend fun getCoupons(
        totalAmount: Long,
        orderProducts: List<Product>,
    ): Result<List<Coupon>>
}
