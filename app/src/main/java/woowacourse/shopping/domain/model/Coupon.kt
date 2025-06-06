package woowacourse.shopping.domain.model

import java.time.LocalDateTime

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: LocalDateTime,
)
