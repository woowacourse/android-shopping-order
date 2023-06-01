package woowacourse.shopping.data.order.response

import woowacourse.shopping.data.order.OrderDataModel

data class OrderResponse(
    val orderId: Int,
    val totalPrice: Int,
    val spendPoint: Int,
    val spendPrice: Int,
    val orderDate: String,
    val orderItems: List<OrderDataModel>
)
