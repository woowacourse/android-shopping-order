package woowacourse.shopping.data.model

data class OrderDetailDto(
    val orderId: Int,
    val orderAt: String,
    val orderStatus: String,
    val payAmount: Int,
    val usedPoint: Int,
    val savedPoint: Int,
    val products: List<OrderProductDto>
)
