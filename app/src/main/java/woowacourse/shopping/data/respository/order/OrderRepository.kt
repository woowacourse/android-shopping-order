package woowacourse.shopping.data.respository.order

import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.OrderPostEntity

interface OrderRepository {
    fun requestPostOrder(
        orderPostEntity: OrderPostEntity,
        onFailure: () -> Unit,
        onSuccess: (orderId: Long) -> Unit,
    )

    fun requestOrderById(
        orderId: Long,
        onFailure: () -> Unit,
        onSuccess: (orderDetailEntity: OrderDetailEntity) -> Unit,
    )

    fun requestOrders(
        onFailure: () -> Unit,
        onSuccess: (orderDetailEntities: List<OrderDetailEntity>) -> Unit,
    )
}
