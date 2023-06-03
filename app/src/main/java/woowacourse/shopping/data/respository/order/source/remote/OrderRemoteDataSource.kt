package woowacourse.shopping.data.respository.order.source.remote

import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

interface OrderRemoteDataSource {
    fun requestPostData(
        order: Order,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun requestOrderItem(
        orderId: Long,
        onFailure: () -> Unit,
        onSuccess: (OrderDetail) -> Unit,
    )

    fun requestOrderList(
        onFailure: () -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit
    )
}
