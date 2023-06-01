package woowacourse.shopping.domain.model

data class Order(
    val id: Int = NO_ID,
    val orderProducts: List<OrderProduct>,
    val payment: Payment,
) {
    val finalPrice: Price = payment.finalPrice

    fun applyPointDiscount(point: Point): Order = copy(
        payment = payment.applyPointDiscount(point)
    )

    companion object {
        private const val NO_ID = -1
    }
}
