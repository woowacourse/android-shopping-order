package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

data class PercentageDiscountCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Float,
    val startTime: LocalTime,
    val endTime: LocalTime
) : Coupon(
    code = code,
    description = description,
    expirationDate = expirationDate,
) {
    override fun calculateDiscountPrice(orderPrice: Int): Int {
        return (orderPrice * discount).toInt()
    }

    fun isDiscountingTime(curTime: LocalTime): Boolean = curTime in (startTime..endTime)
}
