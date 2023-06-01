package woowacourse.shopping.data.order.response

data class OrderDetailDataModel(
    val orderId: Int,
    val totalPrice: Int,
    val spendPoint: Int,
    val spendPrice: Int,
    val orderDate: String,
    val orderItems: List<OrderRequestProductDataModel>
)
