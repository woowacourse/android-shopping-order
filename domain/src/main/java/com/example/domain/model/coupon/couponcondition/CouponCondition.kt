package com.example.domain.model.coupon.couponcondition

import java.time.LocalDate

abstract class CouponCondition(
    private val expirationDate: LocalDate,
) {
    private fun isExpired(useDate: LocalDate): Boolean = useDate > expirationDate

    fun isAvailable(useDate: LocalDate): Boolean = !isExpired(useDate) && condition()

    abstract fun condition(): Boolean
}
