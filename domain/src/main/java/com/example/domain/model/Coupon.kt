package com.example.domain.model

import java.time.LocalDate

sealed class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
) {
    class FixedCoupon(
        id: Int,
        code: String,
        description: String,
        expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon(id, code, description, expirationDate)

    class BuyXGetYCoupon(
        id: Int,
        code: String,
        description: String,
        expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon(id, code, description, expirationDate)

    class FreeShippingCoupon(
        id: Int,
        code: String,
        description: String,
        expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon(id, code, description, expirationDate)

    class PercentageCoupon(
        id: Int,
        code: String,
        description: String,
        expirationDate: LocalDate,
        val discount: Int,
        val availableTime: LocalTimeRange,
    ) : Coupon(id, code, description, expirationDate)

    class DefaultCoupon(
        id: Int,
        code: String,
        description: String,
        expirationDate: LocalDate,
    ) : Coupon(id, code, description, expirationDate)

    companion object {
        const val FIXED_COUPON_TYPE_STRING = "fixed"
        const val BUYX_GETY_COUPON_TYPE_STRING = "buyXgetY"
        const val FREE_SHIPPING_COUPON_TYPE_STRING = "freeShipping"
        const val PERCENTAGE_COUPON_TYPE_STRING = "percentage"
        const val DEFAULT_COUPON_TYPE_STRING = "default"
    }
}
