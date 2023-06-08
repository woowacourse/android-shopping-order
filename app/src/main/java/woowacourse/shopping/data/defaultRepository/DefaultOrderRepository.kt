package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.mapper.OrderMapper.toOrderItemDto
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.RequestOrderDto
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.util.fetchHeaderId
import woowacourse.shopping.util.fetchResponseBody

class DefaultOrderRepository : OrderRepository {
    override fun order(orderItems: List<CartProduct>, callback: (Result<Long>) -> Unit) {
        val requestOrderDto = RequestOrderDto(orderItems.map { it.toOrderItemDto() })
        ServicePool.retrofitService.order(requestOrderDto).fetchHeaderId(
            onSuccess = { callback(Result.success(it)) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchOrders(callback: (Result<List<Order>>) -> Unit) {
        ServicePool.retrofitService.getOrders().fetchResponseBody(
            onSuccess = { callback(Result.success(it.orders)) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun fetchOrder(id: Long, callback: (Result<Order>) -> Unit) {
        ServicePool.retrofitService.getOrder(id).fetchResponseBody(
            onSuccess = { callback(Result.success(it)) },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
