package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: DiscountType,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableStartTime: LocalTime? = null,
    val availableEndTime: LocalTime? = null
) {
    fun calculateDiscount(
        cartItems: List<CartItem>,
        orderAmount: Int,
        shippingFee: Int = 3000
    ): Int {
        return when (discountType) {
            DiscountType.FIXED -> {
                if (minimumAmount != null && orderAmount >= minimumAmount) discount ?: 0 else 0
            }

            DiscountType.BUY_X_GET_Y -> {
                val requiredCount = (buyQuantity ?: 0) + (getQuantity ?: 0)
                val eligibleItems = cartItems.filter { it.quantity >= requiredCount }
                val maxItem = eligibleItems.maxByOrNull { it.goods.price }
                val discountPerUnit = maxItem?.goods?.price ?: 0
                discountPerUnit * (getQuantity ?: 1)
            }

            DiscountType.FREE_SHIPPING -> {
                if (minimumAmount != null && orderAmount >= minimumAmount) shippingFee else 0
            }

            DiscountType.PERCENTAGE -> {
                val now = LocalTime.now()
                if (availableStartTime != null && availableEndTime != null &&
                    now in availableStartTime..availableEndTime
                ) {
                    ((discount ?: 0) * orderAmount / 100.0).toInt()
                } else 0
            }
        }
    }
}