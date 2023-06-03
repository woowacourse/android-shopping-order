package woowacourse.shopping.data.dto

import woowacourse.shopping.domain.model.OrderItems

typealias OrderRequestDto = OrderRequest

data class OrderRequest(
    val orderItems: OrderItems,
    val payment: Payment,
)
