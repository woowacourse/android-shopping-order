package woowacourse.shopping.domain

import java.time.LocalDate
import java.time.LocalDateTime

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: TimeRange?,
) {
    fun isAvailable(
        cartItems: List<CartItem>,
        now: LocalDateTime,
    ): Boolean {
        if (now.toLocalDate().isAfter(expirationDate)) return false
        if (minimumAmount != null && cartItems.sumOf { cartItem -> cartItem.totalPrice } < minimumAmount) return false
        if (availableTime != null && !availableTime.includes(now.toLocalTime())) return false
        if (buyQuantity != null && getQuantity != null && cartItems.none { cartItem -> cartItem.quantity >= buyQuantity }) return false
        return true
    }
}
