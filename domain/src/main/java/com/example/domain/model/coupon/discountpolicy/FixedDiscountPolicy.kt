package com.example.domain.model.coupon.discountpolicy

class FixedDiscountPolicy(
    private val discountAmount: Int,
) : DiscountPolicy {
    override fun getAmount(): Int {
        return discountAmount
    }
}
