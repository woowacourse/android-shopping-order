package woowacourse.shopping.domain.model

class OrderRequest(
    val orderItems: List<OrderItems>,
    val payment: Payment,
)
