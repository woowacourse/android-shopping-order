package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class PercentageCoupon(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val discountRate: Double = 0.3,
    override val expirationDate: LocalDate,
    override val id: String = UUID.randomUUID().toString(),
    override val description: String = "",
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(expirationDate)) return false
        val now = context.now

        return now.isAfter(
            startTime.minusSeconds(1),
        ) &&
            now.isBefore(endTime.plusSeconds(1))
    }

    override fun discountAmount(context: OrderContext): Int {
        if (!isApplicable(context)) return 0
        return (context.totalPrice * discountRate).toInt()
    }
}
