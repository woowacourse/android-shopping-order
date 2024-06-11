package woowacourse.shopping.utils.helper

import woowacourse.shopping.domain.model.coupon.Coupon
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ShoppingDateTimeHelper {
    const val EXPIRATION_FORMAT = "yyyy년 MM월 dd일"
    private const val AVAILABLE_TIME_FORMAT = "HH:mm:ss"

    fun Coupon.isPastDate(): Boolean {
        val formatter = DateTimeFormatter.ofPattern(EXPIRATION_FORMAT)
        val dateToCompare = LocalDate.parse(this.expirationDate, formatter)
        val currentDate = LocalDate.now()
        return currentDate.isAfter(dateToCompare)
    }

    fun Coupon.isAvailableTime(): Boolean {
        return this.availableTime?.let {
            val timeFormatter = DateTimeFormatter.ofPattern(AVAILABLE_TIME_FORMAT)
            val start = LocalTime.parse(it.start, timeFormatter)
            val end = LocalTime.parse(it.end, timeFormatter)
            val currentTime = LocalTime.now()
            if (start.isBefore(end)) {
                currentTime.isAfter(start) && currentTime.isBefore(end)
            } else {
                currentTime.isAfter(start) || currentTime.isBefore(end)
            }
        } ?: true
    }
}
