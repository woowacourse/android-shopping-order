package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.order.Order
import java.time.LocalDate
import java.time.LocalTime

data class TimeBasedPercentCoupon(
    override val code: String,
    override val name: String,
    override val expirationDate: LocalDate,
    val discountRate: Double,
    val startTime: LocalTime,
    val endTime: LocalTime,
) : Coupon(
    code = code,
    name = name,
    expirationDate = expirationDate,
) {
    init {
        require(discountRate in 0.0..1.0) { "할인율은 0 이상 1 이하이어야 합니다." }
    }

    override fun discountAmount(
        order: Order,
        context: CouponContext,
    ): Int =
        if (context.currentTime in startTime..<endTime) {
            (order.orderAmount * discountRate).toInt()
        } else {
            0
        }
}