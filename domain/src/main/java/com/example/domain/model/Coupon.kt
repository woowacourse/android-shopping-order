package com.example.domain.model

import java.time.LocalDate

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate

    data class FixedCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon

    data class BuyXGetYCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShippingCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon

    data class PercentageCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val availableTime: LocalTimeRange,
    ) : Coupon

    data class DefaultCoupon(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
    ) : Coupon

    companion object {
        const val FIXED_COUPON_TYPE_STRING = "fixed"
        const val BUYX_GETY_COUPON_TYPE_STRING = "buyXgetY"
        const val FREE_SHIPPING_COUPON_TYPE_STRING = "freeShipping"
        const val PERCENTAGE_COUPON_TYPE_STRING = "percentage"
        const val DEFAULT_COUPON_TYPE_STRING = "default"
    }
}
