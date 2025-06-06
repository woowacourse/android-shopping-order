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
        now: LocalDateTime = LocalDateTime.now(),
    ): Boolean {
        val isNotExpired = expirationDate >= now.toLocalDate()

        val isAvailableTime =
            availableTime?.let {
                val currentTime = now.toLocalTime()
                currentTime >= it.start && currentTime <= it.end
            } ?: true

        val isAmountSatisfied =
            minimumAmount?.let {
                orderPrice >= it
            } ?: true

        val isBuyXGetYSatisfied =
            (discountType as? DiscountType.BuyXGetY)
                ?.let { items.any { item -> item.quantity >= it.buyQuantity + it.getQuantity } }
                ?: true

        return isNotExpired && isAvailableTime && isAmountSatisfied && isBuyXGetYSatisfied
    }
}
