package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.datasource.response.OrderEntity

interface OrderRemoteDataSource {

    fun addOrder(orderRequest: OrderRequest): Result<Long>

    fun getOrder(
        orderId: Int,
        onReceived: (order: OrderEntity) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun getOrders(
        onReceived: (List<OrderEntity>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )
}
