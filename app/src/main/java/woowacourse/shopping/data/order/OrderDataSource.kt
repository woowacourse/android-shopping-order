package woowacourse.shopping.data.order

import woowacourse.shopping.domain.entity.coupon.Coupons

interface OrderDataSource {
    suspend fun orderProducts(productIds: List<Long>): Result<Unit>

    suspend fun loadDiscountCoupons(): Result<Coupons>
}
