package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Order
import java.time.LocalDate
import java.time.LocalDateTime

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon() {
    override fun checkAvailability(
        order: Order,
        currentDateTime: LocalDateTime,
    ): Boolean {
        return checkExpirationDate(currentDateTime) && isInAvailableTimeRange(currentDateTime)
    }

    override fun checkExpirationDate(currentDateTime: LocalDateTime): Boolean {
        val expirationDateTime = expirationDate.atTime(MAX_HOUR, MAX_MINUTE, MAX_SECOND)
        return currentDateTime.isBefore(expirationDateTime)
    }

    private fun isInAvailableTimeRange(currentDateTime: LocalDateTime): Boolean {
        val currentTime = currentDateTime.toLocalTime()
        return currentTime.isAfter(availableTime.start) && currentTime.isBefore(availableTime.end)
    }

    override fun discountAmount(order: Order): Int {
        val discountPercentage = discount * PERCENTAGE
        return (order.getTotalPrice() * discountPercentage).toInt()
    }

    companion object {
        const val PERCENTAGE = 0.01
    }
}
