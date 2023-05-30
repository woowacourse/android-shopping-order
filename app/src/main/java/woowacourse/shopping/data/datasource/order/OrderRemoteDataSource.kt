package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.data.model.OrderRequest

interface OrderRemoteDataSource {

    fun addOrder(
        orderRequest: OrderRequest,
        onAdded: (orderId: Long) -> Unit,
    )

    fun getOrder(
        orderId: Int,
        onReceived: (order: DataOrder) -> Unit,
    )

    fun getOrders(onReceived: (List<DataOrder>) -> Unit)
}
