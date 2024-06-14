package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: String

    data class Fixed(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon

    data class BuyXGetY(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val minimumAmount: Int,
    ) : Coupon

    data class MiracleSale(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : Coupon

    data class Etc(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val startTime: LocalTime?,
        val endTime: LocalTime?,
        val buyQuantity: Int?,
        val discount: Int?,
        val getQuantity: Int?,
        val minimumAmount: Int?,
    ) : Coupon

    companion object {
        const val DISCOUNT_TYPE_FIXED = "fixed"
        const val DISCOUNT_TYPE_BUYX_GETY = "buyXgetY"
        const val DISCOUNT_TYPE_FREE_SHIPPING = "freeShipping"
        const val DISCOUNT_TYPE_PERCENTAGE = "percentage"
    }
}
