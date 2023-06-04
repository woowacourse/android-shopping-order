package woowacourse.shopping.data.order.model

data class OrderSummaryResponse(
    val id: Long,
    val finalPrice: Long,
    val products: List<String>,
    val orderDate: String
)
