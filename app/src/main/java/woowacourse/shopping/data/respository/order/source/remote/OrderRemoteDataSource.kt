package woowacourse.shopping.data.respository.order.source.remote

import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

interface OrderRemoteDataSource {
    fun requestPostData(
        order: Order,
        onFailure: (message: String) -> Unit,
        onSuccess: (Long) -> Unit,
    )

    fun requestOrderItem(
        orderId: Long,
        onFailure: (message: String) -> Unit,
        onSuccess: (OrderDetail) -> Unit,
    )

    fun requestOrderList(
        onFailure: (message: String) -> Unit,
        onSuccess: (List<OrderDetail>) -> Unit
    )
}
