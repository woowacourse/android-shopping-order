package com.example.domain.model.coupon

import com.example.domain.model.Cart
import com.example.domain.model.coupon.couponcondition.AtLeastMinimumPriceCondition
import com.example.domain.model.coupon.discountpolicy.FixedDiscountPolicy
import com.example.domain.model.coupon.shippingfeepolicy.FreeShippingFeePolicy
import com.example.domain.model.coupon.shippingfeepolicy.ShippingFeePolicy
import java.time.LocalDate

class FreeShippingCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val couponCondition: AtLeastMinimumPriceCondition,
    override val discountPolicy: FixedDiscountPolicy,
    override val shippingFeePolicy: ShippingFeePolicy,
) : Coupon {
    companion object {
        fun FreeShippingCoupon(
            id: Int,
            code: String,
            description: String,
            expirationDate: LocalDate,
            minimumPrice: Int,
            cart: Cart,
        ): FreeShippingCoupon =
            FreeShippingCoupon(
                id,
                code,
                description,
                expirationDate,
                AtLeastMinimumPriceCondition(expirationDate, minimumPrice, cart),
                FixedDiscountPolicy(0),
                FreeShippingFeePolicy,
            )
    }
}
