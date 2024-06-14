package com.example.domain.model.coupon.discountpolicy

import com.example.domain.model.Cart

class PercentDiscountPolicy(
    private val cart: Cart,
    private val percent: Int,
) : DiscountPolicy {
    override fun getAmount(): Int {
        return cart.price * percent / 100
    }
}
