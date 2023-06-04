package woowacourse.shopping.model.order

data class OrderSummaryResponse(
    val id: Long,
    val finalPrice: Long,
    val products: List<String>,
    val orderDate: String
)
