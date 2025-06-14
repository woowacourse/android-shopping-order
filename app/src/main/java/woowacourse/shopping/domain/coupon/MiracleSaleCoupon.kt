package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Payment
import woowacourse.shopping.domain.cart.ShoppingCarts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class MiracleSaleCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon {
    override fun isUsable(
        today: LocalDateTime,
        order: ShoppingCarts,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false
        if (timeElapsed(today)) return false
        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: ShoppingCarts,
    ): Payment {
        val discount = origin.totalPayment * discount / 100
        val totalPayment = origin.totalPayment - discount

        return origin.copy(
            couponDiscount = discount,
            totalPayment = totalPayment,
        )
    }

    private fun timeElapsed(standardTime: LocalDateTime): Boolean {
        val current = standardTime.toLocalTime()
        val within =
            if (availableTime.start <= availableTime.end) {
                current in availableTime.start..availableTime.end
            } else {
                current >= availableTime.start || current <= availableTime.end
            }
        return !within
    }

    data class AvailableTime(
        val start: LocalTime,
        val end: LocalTime,
    )
}
