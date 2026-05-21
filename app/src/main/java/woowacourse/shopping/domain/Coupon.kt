package woowacourse.shopping.domain

import java.time.LocalDate

data class Coupon(
    val code: CouponCode,
    val title: String,
    val discountPrice: Int,
    val discountRatio: Float,
    val minimumPrice: Int,
    val buyCount: Int,
    val serviceCount: Int,
    val availableStartTime: Int,
    val availableEndTime: Int,
    val expiryDate: LocalDate
)
