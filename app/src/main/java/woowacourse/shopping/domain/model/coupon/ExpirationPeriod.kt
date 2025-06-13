package woowacourse.shopping.domain.model.coupon

import java.time.LocalDate
import java.time.LocalTime

data class ExpirationPeriod(
    val startDate: LocalDate = LocalDate.MIN,
    val endDate: LocalDate = LocalDate.MAX,
    val startTime: LocalTime = LocalTime.MIN,
    val endTime: LocalTime = LocalTime.MAX,
) {
    val isValidTime: Boolean
        get() = isValidDate() && isValidTimeRange()

    private fun isValidDate(): Boolean {
        val currentDate = LocalDate.now()
        return when {
            currentDate in startDate..endDate -> true
            else -> false
        }
    }

    private fun isValidTimeRange(): Boolean {
        val currentTime = LocalTime.now()
        return when {
            currentTime in startTime..endTime -> true
            else -> false
        }
    }
}
