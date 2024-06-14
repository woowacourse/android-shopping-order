package com.example.domain.model.coupon

import com.example.domain.model.Cart
import com.example.domain.model.coupon.couponcondition.AtLeastMinimumPriceCondition
import com.example.domain.model.coupon.discountpolicy.FixedDiscountPolicy
import com.example.domain.model.coupon.shippingfeepolicy.DefaultShippingFeePolicy
import com.example.domain.model.coupon.shippingfeepolicy.ShippingFeePolicy
import java.time.LocalDate

class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val couponCondition: AtLeastMinimumPriceCondition,
    override val discountPolicy: FixedDiscountPolicy,
    override val shippingFeePolicy: ShippingFeePolicy,
) : Coupon {
    companion object {
        fun FixedCoupon(
            id: Int,
            code: String,
            description: String,
            expirationDate: LocalDate,
            cart: Cart,
            minimumAmount: Int,
            discountAmount: Int,
        ): FixedCoupon =
            FixedCoupon(
                id,
                code,
                description,
                expirationDate,
                AtLeastMinimumPriceCondition(expirationDate, minimumAmount, cart),
                FixedDiscountPolicy(discountAmount),
                DefaultShippingFeePolicy,
            )
    }
}
