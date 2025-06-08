package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed class Discount {
    data class FixedAmount(
        val discountAmount: Int,
        val minimumAmount: Int,
    ) : Discount()

    data class BuyXGetYFree(
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Discount()

    data class FreeShipping(
        val minimumAmount: Int,
    ) : Discount()

    data class Percentage(
        val discountPercentage: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : Discount()
}
