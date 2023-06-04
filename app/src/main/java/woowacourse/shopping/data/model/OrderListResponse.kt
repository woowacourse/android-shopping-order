package woowacourse.shopping.data.model

import woowacourse.shopping.model.OrderHistory

data class OrderListResponse(
    val orders: List<OrderHistory>,
    val lastOrderId: Int,
)
