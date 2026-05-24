package woowacourse.shopping.domain.model

import java.time.LocalDate

sealed class CouponInfo {
    abstract val code: String
    abstract val expirationDate: LocalDate
    abstract val minimumOrderAmount: Long?

    data class Fixed(
        override val code: String,
        override val expirationDate: LocalDate,
        override val minimumOrderAmount: Long,
        val discountAmount: Long,
    ) : CouponInfo()

    data class Percentage(
        override val code: String,
        override val expirationDate: LocalDate,
        override val minimumOrderAmount: Long? = null,
        val discountRate: Int,
    ) : CouponInfo()

    data class BuyXGetY(
        override val code: String,
        override val expirationDate: LocalDate,
        override val minimumOrderAmount: Long? = null,
        val buyQuantity: Long,
        val freeGetQuantity: Long,
    ) : CouponInfo()

    data class FreeShipping(
        override val code: String,
        override val expirationDate: LocalDate,
        override val minimumOrderAmount: Long,
    ) : CouponInfo()
}
