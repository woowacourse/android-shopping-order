package woowacourse.shopping

class Payment(
    val totalPrice: Price,
    val usablePoint: Point
) {
    fun discountable(usingPoint: Point): Boolean {
        return totalPrice.value >= usingPoint.value
    }

    fun discount(usingPoint: Point): Price {
        return Price(totalPrice.value - usingPoint.value)
    }
}
