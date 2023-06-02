package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.datasource.response.OrderResponse

interface OrderRemoteDataSource {

    fun addOrder(
        orderRequest: OrderRequest,
        onAdded: (orderId: Long) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun getOrder(
        orderId: Int,
        onReceived: (order: OrderResponse) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun getOrders(
        onReceived: (List<OrderResponse>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )
}
