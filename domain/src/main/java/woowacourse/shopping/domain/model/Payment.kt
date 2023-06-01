package woowacourse.shopping.domain.model

data class Payment(
    val originalPayment: Price,
    val finalPayment: Price,
    val usedPoint: Point = Point(0),
) {

    fun applyPointDiscount(point: Point): Payment = copy(
        finalPayment = finalPayment - point.value,
        usedPoint = point
    )

    companion object {
        fun of(originalPayment: Price): Payment = Payment(
            originalPayment = originalPayment,
            finalPayment = originalPayment,
        )
    }
}
