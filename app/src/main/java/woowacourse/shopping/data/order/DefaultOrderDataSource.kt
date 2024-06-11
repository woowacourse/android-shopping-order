package woowacourse.shopping.data.order

import woowacourse.shopping.domain.entity.coupon.Coupons
import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.service.OrderService

class DefaultOrderDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orderProducts(productIds: List<Long>): Result<Unit> {
        return runCatching {
            orderService.orderProducts(OrderRequest(productIds))
        }
    }

    override suspend fun loadDiscountCoupons(): Result<Coupons> {
        return runCatching {
            orderService.loadDiscountCoupons().toDomain()
        }
    }
}
