package woowacourse.shopping.domain.model

import java.time.LocalDate

data class Coupon(
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
    val availableTime: AvailableTime,
)
