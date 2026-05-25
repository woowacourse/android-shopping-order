package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.Price
import java.time.LocalDate
import java.time.LocalDateTime

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
) : Coupon() {
    override fun canApply(
        order: Order,
        now: LocalDateTime,
    ): Boolean {
        if (isExpired(now.toLocalDate())) return false
        return order.totalAmount().toInt() >= minimumAmount
    }

    override fun discountAmount(
        order: Order,
        now: LocalDateTime,
    ): Price {
        if (!canApply(order, now)) return Price(0)
        return Price(3_000)
    }
}
