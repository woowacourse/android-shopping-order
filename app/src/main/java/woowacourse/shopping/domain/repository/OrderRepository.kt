package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.coupon.Coupons

interface OrderRepository {
    suspend fun orderCartProducts(productIds: List<Long>): Result<Unit>

    suspend fun loadDiscountCoupons(): Result<Coupons>
}