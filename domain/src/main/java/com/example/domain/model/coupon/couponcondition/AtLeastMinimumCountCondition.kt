package com.example.domain.model.coupon.couponcondition

import com.example.domain.model.Cart
import java.time.LocalDate

class AtLeastMinimumCountCondition(
    expirationDate: LocalDate,
    private val minimumAmount: Int,
    private val cart: Cart,
) : CouponCondition(expirationDate) {
    override fun condition(): Boolean = cart.contains { it.quantity.count >= minimumAmount }
}
