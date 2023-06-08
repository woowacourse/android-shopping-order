package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.mapper.OrderMapper.toOrderItemDto
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.RequestOrderDto
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.util.fetchHeaderId
import woowacourse.shopping.util.fetchResponseBody

class DefaultOrderRepository : OrderRepository {
    override fun order(orderItems: List<CartProduct>, callback: (WoowaResult<Long>) -> Unit) {
        val requestOrderDto = RequestOrderDto(orderItems.map { it.toOrderItemDto() })
        ServicePool.retrofitService.order(requestOrderDto).fetchHeaderId(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun fetchOrders(callback: (WoowaResult<List<Order>>) -> Unit) {
        ServicePool.retrofitService.getOrders().fetchResponseBody(
            onSuccess = { callback(WoowaResult.SUCCESS(it.orders)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun fetchOrder(id: Long, callback: (WoowaResult<Order>) -> Unit) {
        ServicePool.retrofitService.getOrder(id).fetchResponseBody(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }
}
