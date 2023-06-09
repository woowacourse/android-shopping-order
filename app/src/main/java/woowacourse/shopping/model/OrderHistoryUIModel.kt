package woowacourse.shopping.model

data class OrderHistoryUIModel(
    val id: Long,
    val orderItems: List<OrderHistoryItemUIModel>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String
)
