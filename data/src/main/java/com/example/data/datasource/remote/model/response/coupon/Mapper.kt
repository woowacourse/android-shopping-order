package com.example.data.datasource.remote.model.response.coupon

import com.example.domain.model.Coupon
import com.example.domain.model.LocalTimeRange
import java.time.LocalDate
import java.time.LocalTime

fun CouponResponse.toCoupon(): Coupon =
    when (this.discountType) {
        Coupon.FIXED_COUPON_TYPE_STRING ->
            Coupon.FixedCoupon(
                id,
                code,
                description,
                LocalDate.parse(expirationDate),
                discount,
                minimumAmount,
            )

        Coupon.BUYX_GETY_COUPON_TYPE_STRING ->
            Coupon.BuyXGetYCoupon(
                id,
                code,
                description,
                LocalDate.parse(expirationDate),
                buyQuantity,
                getQuantity,
            )

        Coupon.FREE_SHIPPING_COUPON_TYPE_STRING ->
            Coupon.FreeShippingCoupon(
                id,
                code,
                description,
                LocalDate.parse(expirationDate),
                minimumAmount,
            )

        Coupon.PERCENTAGE_COUPON_TYPE_STRING ->
            Coupon.PercentageCoupon(
                id,
                code,
                description,
                LocalDate.parse(expirationDate),
                discount,
                availableTime.toLocalTimeRange(),
            )

        else ->
            Coupon.DefaultCoupon(
                id,
                code,
                description,
                LocalDate.parse(expirationDate),
            )
    }

fun AvailableTime.toLocalTimeRange(): LocalTimeRange = LocalTimeRange(LocalTime.parse(start), LocalTime.parse(end))
