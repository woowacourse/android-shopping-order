package woowacourse.shopping.domain.model

class OrderRequest(
    val orderItems: OrderItems,
    val payment: Payment,
)
