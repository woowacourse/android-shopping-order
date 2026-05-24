package woowacourse.shopping.domain.coupon

import woowacourse.shopping.data.network.coupon.dto.AvailableTime
import java.time.LocalDate
import java.time.LocalTime

data class PercentageDiscountCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon(
        code = code,
        description = description,
        expirationDate = expirationDate,
    ) {
    override fun calculateDiscountPrice(orderPrice: Int): Int = (orderPrice * discount) / 100

    fun isDiscountingTime(curTime: LocalTime): Boolean {
        val startTime = LocalTime.parse(availableTime.start)
        val endTime = LocalTime.parse(availableTime.end)
        return curTime.isAfter(startTime) && curTime.isBefore(endTime)
    }
}
