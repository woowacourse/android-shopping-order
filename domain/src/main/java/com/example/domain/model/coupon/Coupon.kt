package com.example.domain.model.coupon

import com.example.domain.model.Receipt
import com.example.domain.model.coupon.couponcondition.CouponCondition
import com.example.domain.model.coupon.discountpolicy.DiscountPolicy
import com.example.domain.model.coupon.shippingfeepolicy.ShippingFeePolicy
import java.time.LocalDate

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val couponCondition: CouponCondition
    val discountPolicy: DiscountPolicy
    val shippingFeePolicy: ShippingFeePolicy

    fun apply(
        useDate: LocalDate,
        receipt: Receipt,
    ): Receipt =
        if (couponCondition.isAvailable(useDate)) {
            Receipt(
                price = receipt.price,
                discountPolicy.getAmount(),
                shippingFeePolicy.getAmount(),
            )
        } else {
            receipt
        }
}
