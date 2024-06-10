package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate
import java.time.LocalDateTime

data class Coupon(
    val id: Long,
    val code: CouponCode,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: String,
    val discount: Int = 0,
    val minimumAmount: Int = 0,
    val buyQuantity: Int = 0,
    val getQuantity: Int = 0,
    val availableTime: AvailableTime? = null,
    private val orderAmount: Int = 0,
    private val currentDateTime: LocalDateTime = LocalDateTime.now(),
)
