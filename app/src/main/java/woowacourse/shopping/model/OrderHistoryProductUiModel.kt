package woowacourse.shopping.model

data class OrderHistoryProductUiModel(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val orderStatus: OrderStateUiModel,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)
