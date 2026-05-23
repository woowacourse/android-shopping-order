package woowacourse.shopping.domain.coupon

import java.time.LocalDate
import java.time.LocalTime

class MiracleMorningCoupon(
    override val validUntil: LocalDate,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        val now = context.now

        return now.isAfter(LocalTime.of(3, 59, 59)) && now.isBefore(LocalTime.of(7, 0, 1))
    }

    override fun discountAmount(context: OrderContext): Int {
        if (!isApplicable(context)) return 0
        val totalPrice = context.totalPrice

        return (totalPrice * DISCOUNT_RATE).toInt()
    }

    companion object {
        const val DISCOUNT_RATE = 0.3
    }
}
