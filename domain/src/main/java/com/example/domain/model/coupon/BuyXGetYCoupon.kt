package com.example.domain.model.coupon

import com.example.domain.model.Cart
import com.example.domain.model.coupon.couponcondition.AtLeastMinimumCountCondition
import com.example.domain.model.coupon.couponcondition.CouponCondition
import com.example.domain.model.coupon.discountpolicy.BuyXGetYDiscountPolicy
import com.example.domain.model.coupon.discountpolicy.DiscountPolicy
import com.example.domain.model.coupon.shippingfeepolicy.DefaultShippingFeePolicy
import com.example.domain.model.coupon.shippingfeepolicy.ShippingFeePolicy
import java.time.LocalDate

class BuyXGetYCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val couponCondition: CouponCondition,
    override val discountPolicy: DiscountPolicy,
    override val shippingFeePolicy: ShippingFeePolicy,
) : Coupon {
    companion object {
        fun BuyXGetYCoupon(
            id: Int,
            code: String,
            description: String,
            expirationDate: LocalDate,
            buyAmount: Int,
            getAmount: Int,
            cart: Cart,
        ): BuyXGetYCoupon =
            BuyXGetYCoupon(
                id,
                code,
                description,
                expirationDate,
                AtLeastMinimumCountCondition(expirationDate, buyAmount, cart),
                BuyXGetYDiscountPolicy(cart, buyAmount, getAmount),
                DefaultShippingFeePolicy,
            )
    }
}
