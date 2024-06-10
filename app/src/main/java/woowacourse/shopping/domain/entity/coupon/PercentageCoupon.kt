package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart
import java.time.LocalDateTime
import java.time.LocalTime

data class PercentageCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDateTime,
    val discountRate: Float,
    val availableStartTime: LocalTime,
    val availableEndTime: LocalTime,
) : Coupon(id, code, description, 0, expirationDate) {
    override fun available(cart: Cart, targetDateTime: LocalDateTime): Boolean {
        return !isExpired(targetDateTime) &&
                targetDateTime.toLocalTime()
                    .isBetween(availableStartTime, availableEndTime)
    }

    private fun LocalTime.isBetween(
        start: LocalTime,
        end: LocalTime,
    ): Boolean {
        return (this.isBefore(start) || this.isAfter(end)).not()
    }

    override fun calculateDiscount(
        cart: Cart,
        shippingFee: Long,
    ): DiscountResult {
        return DiscountResult(cart.totalPrice(), cart.totalPrice().discount(), shippingFee)
    }

    private fun Long.discount(): Long {
        return (this * discountRate).toLong()
    }
}
