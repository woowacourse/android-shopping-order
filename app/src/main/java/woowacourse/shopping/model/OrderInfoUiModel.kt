package woowacourse.shopping.model

data class OrderInfoUiModel(
    val orderId: Int,
    val orderAt: String,
    val orderState: OrderStateUiModel,
    val payAmount: Int,
    val usedPoint: Int,
    val savedPoint: Int,
    val products: List<OrderDetailProductUiModel>
)
