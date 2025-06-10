package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Coupon(
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val discount: Int?,
    val discountType: DiscountType,
    val minimumAmount: Int?,
    val availableTime: AvailableTime?,
) {
    fun isValidForOrder(
        orderPrice: Int,
        items: List<CartItem>,
        orderTime: LocalDateTime = LocalDateTime.now(),
    ): Boolean {
        val isNotExpired = expirationDate >= orderTime.toLocalDate()

        val isAvailableTime =
            availableTime?.let {
                val currentTime = orderTime.toLocalTime()
                currentTime >= it.start && currentTime <= it.end
            } ?: true

        val isAmountSatisfied =
            minimumAmount?.let {
                orderPrice >= it
            } ?: true

        val isBuyXGetYSatisfied =
            if (this.buyQuantity == null || this.getQuantity == null) {
                false
            } else {
                items.any { item -> item.quantity >= this.buyQuantity + this.getQuantity }
            }

        return isNotExpired && isAvailableTime && isAmountSatisfied && isBuyXGetYSatisfied
    }
}
