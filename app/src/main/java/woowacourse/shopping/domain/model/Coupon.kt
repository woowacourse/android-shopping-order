package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: String,
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
            "fixed" -> {
                if (minimumAmount != null && orderAmount >= minimumAmount) discount ?: 0 else 0
            }

            "buyXgetY" -> {
                val requiredCount = (buyQuantity ?: 0) + (getQuantity ?: 0)
                val eligibleItems = cartItems.filter { it.quantity >= requiredCount }

                val maxItem = eligibleItems.maxByOrNull { it.goods.price }
                val discountPerUnit = maxItem?.goods?.price ?: 0

                discountPerUnit*(getQuantity?:1)
            }
            "freeShipping" -> {
                if (minimumAmount != null && orderAmount >= minimumAmount) shippingFee else 0
            }

            "percentage" -> {
                val now = LocalTime.now()
                if (availableStartTime != null && availableEndTime != null &&
                    now in availableStartTime..availableEndTime
                ) {
                    ((discount ?: 0) * orderAmount / 100.0).toInt()
                } else 0
            }

            else -> 0
        }
    }
}