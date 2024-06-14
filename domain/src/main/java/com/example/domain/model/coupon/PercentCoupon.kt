package com.example.domain.model.coupon

import com.example.domain.model.Cart
import com.example.domain.model.LocalTimeRange
import com.example.domain.model.coupon.couponcondition.CouponCondition
import com.example.domain.model.coupon.couponcondition.LimitedTimeCondition
import com.example.domain.model.coupon.discountpolicy.DiscountPolicy
import com.example.domain.model.coupon.discountpolicy.PercentDiscountPolicy
import com.example.domain.model.coupon.shippingfeepolicy.DefaultShippingFeePolicy
import com.example.domain.model.coupon.shippingfeepolicy.ShippingFeePolicy
import java.time.LocalDate
import java.time.LocalTime

class PercentCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val couponCondition: CouponCondition,
    override val discountPolicy: DiscountPolicy,
    override val shippingFeePolicy: ShippingFeePolicy,
) : Coupon {
    companion object {
        fun PercentCoupon(
            id: Int,
            code: String,
            description: String,
            expirationDate: LocalDate,
            buyTime: LocalTime,
            localTimeRange: LocalTimeRange,
            cart: Cart,
            discountPercent: Int,
        ): PercentCoupon =
            PercentCoupon(
                id,
                code,
                description,
                expirationDate,
                LimitedTimeCondition(expirationDate, buyTime, localTimeRange),
                PercentDiscountPolicy(cart, discountPercent),
                DefaultShippingFeePolicy,
            )
    }
}
