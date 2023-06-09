package woowacourse.shopping.domain.model

data class OrderRequest(
    val orderItems: List<OrderItems>,
    val payment: Payment,
)
