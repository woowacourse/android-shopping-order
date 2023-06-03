package woowacourse.shopping.data.model

data class OrderDto(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val orderStatus: String,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)
