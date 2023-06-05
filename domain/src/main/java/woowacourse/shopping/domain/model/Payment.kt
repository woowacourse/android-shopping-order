package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.discount.Discountable
import woowacourse.shopping.domain.model.discount.Point

data class Payment(
    val originalPrice: Price,
    val finalPrice: Price,
    val usedPoint: Discountable = Point(0),
) {

    fun discount(point: Discountable): Payment = copy(
        finalPrice = point.discount(originalPrice),
        usedPoint = point
    )

    companion object {
        fun of(originalPrice: Price): Payment = Payment(
            originalPrice = originalPrice,
            finalPrice = originalPrice,
        )
    }
}
