package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.model.Page
import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse

interface OrderDataSource {
    fun orderProducts(
        token: String,
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestOrders(
        token: String,
        page: Page,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestSpecificOrder(
        token: String,
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (String) -> Unit,
    )
}
