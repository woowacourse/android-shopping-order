package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.Price
import java.time.LocalDate
import java.time.LocalDateTime

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val discountPercentage: Int,
    val availableTime: AvailableTime,
) : Coupon() {
    init {
        require(discountPercentage in 1..100) { "할인율은 1에서 100 사이의 숫자이여야 합니다." }
    }

    override fun canApply(
        order: Order,
        now: LocalDateTime,
    ): Boolean {
        if (isExpired(now.toLocalDate())) return false
        return availableTime.contains(now.toLocalTime())
    }

    override fun discountAmount(
        order: Order,
        now: LocalDateTime,
    ): Price {
        if (!canApply(order, now)) return Price(0)

        val discountValue = order.totalAmount().toInt() * (discountPercentage / 100.0)
        return Price(discountValue.toInt())
    }
}
