package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderItems
import woowacourse.shopping.domain.model.OrderResponse

interface OrderProductRepository {
    fun orderProduct(
        orderItems: OrderItems,
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
