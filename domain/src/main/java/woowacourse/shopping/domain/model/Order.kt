package woowacourse.shopping.domain.model

data class Order(
    val orderProducts: List<OrderProduct>,
    val totalPayment: Price,
    val usePoint: Point = Point(0),
) {

}
