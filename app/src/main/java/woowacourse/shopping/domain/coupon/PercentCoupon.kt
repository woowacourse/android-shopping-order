package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Order
import java.time.LocalDate
import java.time.LocalTime

data class PercentCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discountPercent: Long,
    val startTime: LocalTime,
    val endTime: LocalTime,
) : Coupon {
    override fun isEligible(order: Order): Boolean {
        if(isExpired(order.currentTime)) return false
        val time = order.currentTime.toLocalTime()
        return time in startTime..endTime
    }

    override fun calculateDiscount(order: Order): Discount {
        val discountAmount = (order.totalProductPrice * discountPercent).toInt()
        return Discount(productDiscount = discountAmount)
    }

}
