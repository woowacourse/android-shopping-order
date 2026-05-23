package woowacourse.shopping.feature.purchase

import java.time.LocalDate
import java.time.LocalTime

sealed class CouponUiModel {
    abstract val id: String
    abstract val description: String
    abstract val expirationDate: LocalDate

    data class FixedDiscount(
        override val id: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumPrice: Int,
    ) : CouponUiModel()

    data class FreeShipping(
        override val id: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumPrice: Int,
    ) : CouponUiModel()

    data class BuyXGetY(
        override val id: String,
        override val description: String,
        override val expirationDate: LocalDate,
    ) : CouponUiModel()

    data class Percentage(
        override val id: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : CouponUiModel()
}
