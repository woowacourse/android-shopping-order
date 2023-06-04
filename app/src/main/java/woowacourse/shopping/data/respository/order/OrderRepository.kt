package woowacourse.shopping.data.respository.order

import woowacourse.shopping.data.model.OrderPostEntity

interface OrderRepository {
    fun requestOrder(
        orderPostEntity: OrderPostEntity,
        onFailure: () -> Unit,
        onSuccess: (orderId: Long) -> Unit,
    )
}
