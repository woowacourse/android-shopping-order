package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse

interface OrderProductRepository {
    fun orderProduct(
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )

    fun requestOrders(
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: () -> Unit,
    )

    fun requestSpecificOrder(
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: () -> Unit,
    )
}
