
package woowacourse.shopping.domain.model

import java.time.LocalDate

data class PercentageCoupon(
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon() {
    override fun isSatisfiedPolicy(orders: Orders): Boolean =
        orders.orderDateTime.toLocalTime() in (availableTime.startTime..availableTime.endTime)

    override fun discountAmount(orders: Orders): Int = (orders.totalPrice * discount / 100)
}
