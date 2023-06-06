package woowacourse.shopping.model

data class OrderHistories(
    val orderHistories: List<OrderHistory>,
    val lastOrderId: Long
)
