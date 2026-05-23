package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

class MiracleMorningCoupon(
    override val validUntil: LocalDate,
    val discountRate: Double = 0.3,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        if (LocalDate.now().isAfter(validUntil)) return false
        val now = context.now

        return now.isAfter(LocalTime.of(3, 59, 59)) && now.isBefore(LocalTime.of(7, 0, 1))
    }

    override fun discountAmount(context: OrderContext): Int {
        if (!isApplicable(context)) return 0
        return (context.totalPrice * discountRate).toInt()
    }
}
