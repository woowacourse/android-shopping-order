package woowacourse.shopping.data.cart.order

import woowacourse.shopping.remote.dto.request.OrderRequest
import woowacourse.shopping.remote.service.OrderService

class DefaultOrderDataSource(
    private val orderService: OrderService,
) : OrderDataSource {
    override suspend fun orderProducts(productIds: List<Long>): Result<Unit> {
        val response = orderService.orderProducts(OrderRequest(productIds))
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
        }
    }
}
