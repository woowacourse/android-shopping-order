package woowacourse.shopping.domain.coupon

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
    fun isUsable(standardTime: LocalDateTime): Boolean {
        if (isExpired(standardTime.toLocalDate())) return false
        if (timeElapsed(standardTime)) return false
        return true
    }

    private fun timeElapsed(standardTime: LocalDateTime): Boolean {
        val current = standardTime.toLocalTime()
        val within = if (availableTime.start <= availableTime.end) {
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
