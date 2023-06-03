package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderItems
import woowacourse.shopping.domain.model.OrderResponse

interface OrderProductRepository {
    fun orderProduct(
        token: String,
        orderItems: OrderItems,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )

    fun requestOrders(
        token: String,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: () -> Unit,
    )

    fun requestSpecificOrder(
        token: String,
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: () -> Unit,
    )
}
