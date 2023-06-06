package woowacourse.shopping.data.remote.order.response

data class OrderRequestDataModel(
    val spendPoint: Int,
    val orderItems: List<OrderCartDataModel>
)
