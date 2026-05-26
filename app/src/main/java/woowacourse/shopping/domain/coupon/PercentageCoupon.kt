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
        if (context.now.toLocalDate().isAfter(expirationDate)) return false
        val time = context.now.toLocalTime()

        return time.isAfter(startTime) && time.isBefore(endTime)
    }

    override fun discountAmount(context: OrderContext): DiscountResult {
        if (!isApplicable(context)) return DiscountResult()
        return DiscountResult(
            couponDiscountPrice = (context.totalPrice * discountRate).toInt(),
            shippingDiscountPrice = context.shippingFee,
        )
    }
}
