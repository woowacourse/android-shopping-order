package woowacourse.shopping.data.remote.order.requestbody

data class OrderRequestBody(
    val spendPoint: Int,
    val orderItems: List<OrderCartRequestBody>
)
