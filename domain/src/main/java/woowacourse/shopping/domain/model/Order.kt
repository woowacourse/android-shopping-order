package woowacourse.shopping.domain.model

data class Order(
    val id: Int = NO_ID,
    val orderProducts: List<OrderProduct>,
    val totalPayment: Price,
    val usePoint: Point = Point(0),
) {
    fun applyPointDiscount(point: Point): Order = copy(
        totalPayment = totalPayment - point.value,
        usePoint = point
    )

    companion object {
        private const val NO_ID = -1
    }
}
