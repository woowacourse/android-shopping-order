package woowacourse.shopping.data.order

import woowacourse.shopping.domain.entity.coupon.Coupons
import woowacourse.shopping.domain.repository.OrderRepository

class DefaultOrderRepository(private val orderDataSource: OrderDataSource) : OrderRepository {
    override suspend fun orderCartProducts(productIds: List<Long>): Result<Unit> {
        return orderDataSource.orderProducts(productIds)
    }

    override suspend fun loadDiscountCoupons(): Result<Coupons> {
        return orderDataSource.loadDiscountCoupons()
    }
}
