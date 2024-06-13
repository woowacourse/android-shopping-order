package com.example.domain.model.coupon.couponcondition

import com.example.domain.model.LocalTimeRange
import java.time.LocalDate
import java.time.LocalTime

class LimitedTimeCondition(
    expirationDate: LocalDate,
    private val buyTime: LocalTime,
    private val timeRange: LocalTimeRange,
) : CouponCondition(expirationDate) {
    override fun condition(): Boolean = buyTime in timeRange
}
