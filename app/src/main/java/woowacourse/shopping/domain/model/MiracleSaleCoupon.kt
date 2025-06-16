package woowacourse.shopping.domain.model

import woowacourse.shopping.data.remote.coupon.AvailableTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        order: Carts,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false

        try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val startTime = LocalTime.parse(availableTime.start, formatter)
            val endTime = LocalTime.parse(availableTime.end, formatter)

            val currentLocalTime = today.toLocalTime()

            if (currentLocalTime.isBefore(startTime) || currentLocalTime.isAfter(endTime)) {
                return false
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: Carts,
    ): Payment {
        val discountAmount = (origin.originPayment * (discount / 100.0)).toInt()

        val newTotalPayment = (origin.originPayment - discountAmount) + origin.deliveryFee

        return origin.copy(
            couponDiscount = -discountAmount,
            totalPayment = newTotalPayment.coerceAtLeast(0),
        )
    }
}
