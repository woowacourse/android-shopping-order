package woowacourse.shopping.data.remote.order.response

data class OrderDetailDataModel(
    val orderId: Int,
    val totalPrice: Int,
    val spendPoint: Int,
    val spendPrice: Int,
    val createdAt: String,
    val orderItemResponses: List<OrderDetailProductDataModel>
)
