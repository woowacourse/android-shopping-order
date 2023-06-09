package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderProductRepository

class OrderProductRemoteRepository(
    private val orderProductDataSource: OrderDataSource,
) : OrderProductRepository {
    override fun orderProduct(
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        orderProductDataSource.orderProducts(
            orderRequest = orderRequest,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun requestOrders(
        page: Page,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        orderProductDataSource.requestOrders(
            page = page.toData(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun requestSpecificOrder(
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        orderProductDataSource.requestSpecificOrder(
            orderId = orderId,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
