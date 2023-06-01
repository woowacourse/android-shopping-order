package woowacourse.shopping.data.order.response

data class OrderRequestDataModel(
    val spendPoint: Int,
    val orderItems: List<OrderCartDataModel>
)
