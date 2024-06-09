package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon(
    open val id: Int,
    open val code: String,
    open val description: String,
    open val expirationDate: LocalDate,
    open val discountType: String,
) {
    data class Fixed(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon(id, code, description, expirationDate, discountType)

    data class BuyXGetY(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon(id, code, description, expirationDate, discountType)

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val minimumAmount: Int,
    ) : Coupon(id, code, description, expirationDate, discountType)

    data class MiracleSale(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : Coupon(id, code, description, expirationDate, discountType)

    companion object {
        const val DISCOUNT_TYPE_FIXED = "fixed"
        const val DISCOUNT_TYPE_BUYX_GETY = "buyXgetY"
        const val DISCOUNT_TYPE_FREE_SHIPPING = "freeShipping"
        const val DISCOUNT_TYPE_PERCENTAGE = "percentage"
    }
}
