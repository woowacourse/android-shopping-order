package woowacourse.shopping.dto

data class OrderHistoryDto(
    val id: Long,
    val orderItems: List<OrderHistoryItemDto>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String
) {
    fun toDomain() = woowacourse.shopping.model.OrderHistory(
        id = id,
        orderItems = orderItems.map { it.toDomain() },
        totalPrice = totalPrice,
        payPrice = payPrice,
        earnedPoints = earnedPoints,
        usedPoints = usedPoints,
        orderDate = orderDate
    )
}
