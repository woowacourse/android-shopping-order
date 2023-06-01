package woowacourse.shopping.domain.model

data class Payment(
    val originalPrice: Price,
    val finalPrice: Price,
    val usedPoint: Point = Point(0),
) {

    fun applyPointDiscount(point: Point): Payment = copy(
        finalPrice = finalPrice - point.value,
        usedPoint = point
    )

    companion object {
        fun of(originalPrice: Price): Payment = Payment(
            originalPrice = originalPrice,
            finalPrice = originalPrice,
        )
    }
}
