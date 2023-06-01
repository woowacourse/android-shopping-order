package woowacourse.shopping.data.order.requestbody

data class OrderRequestBody(
    val spendPoint: Int,
    val orderItems: List<OrderCartRequestBody>
)
