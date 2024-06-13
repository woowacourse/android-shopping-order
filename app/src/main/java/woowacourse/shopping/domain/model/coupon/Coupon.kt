package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.ItemSelector
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Coupon(
    val id: Int,
    val expirationDate: String,
    val couponType: CouponType,
    val description: String,
    val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: AvailableTime?,
    val itemSelector: ItemSelector = ItemSelector(),
) {
    fun isPastDate(): Boolean {
        val formatter = DateTimeFormatter.ofPattern(EXPIRATION_FORMAT)
        val dateToCompare = LocalDate.parse(this.expirationDate, formatter)
        val currentDate = LocalDate.now()
        return currentDate.isAfter(dateToCompare)
    }

    fun isAvailableTime(): Boolean {
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

    companion object {
        const val AVAILABLE_TIME_FORMAT = "HH:mm:ss"
        const val EXPIRATION_FORMAT = "yyyy년 MM월 dd일"
    }
}
