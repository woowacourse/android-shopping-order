package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.model.page.Page

interface OrderProductRepository {
    fun orderProduct(
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestOrders(
        page: Page,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: (String) -> Unit,
    )

    fun requestSpecificOrder(
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (String) -> Unit,
    )
}
