package woowacourse.shopping.uimodel

data class OrderHistoryUIModel(
    val id: Int,
    val orderItems: List<OrderProductUIModel>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String,
)
