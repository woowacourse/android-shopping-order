package woowacourse.shopping.domain.model

import java.time.LocalTime

data class CouponDetail(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: LocalTime,
)
