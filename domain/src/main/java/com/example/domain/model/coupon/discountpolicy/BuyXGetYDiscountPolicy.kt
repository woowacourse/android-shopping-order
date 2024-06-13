package com.example.domain.model.coupon.discountpolicy

import com.example.domain.model.Cart

class BuyXGetYDiscountPolicy(
    private val cart: Cart,
    private val buyAmount: Int,
    private val getAmount: Int,
) : DiscountPolicy {
    override fun getAmount(): Int =
        getAmount *
            cart.filter { it.quantity.count >= buyAmount }
                .mostExpensiveItem()
                .product.price
}
