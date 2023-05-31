package woowacourse.shopping.domain.model

data class Order(
    val orderProducts: List<OrderProduct>,
    val totalPayment: Price,
    val usePoint: Point = Point(0),
) {
    fun applyPointDiscount(point: Point): Order = copy(
        totalPayment = totalPayment - point.value,
        usePoint = point
    )
}
