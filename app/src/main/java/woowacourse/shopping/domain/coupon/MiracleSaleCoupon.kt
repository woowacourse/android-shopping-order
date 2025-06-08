package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Cart
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MiracleSaleCoupon(
    override val couponId: Long,
    override val expirationDate: LocalDate,
    private val startHour: LocalTime,
    private val endHour: LocalTime,
    private val discountRate: Double,
) : Coupon {
    override fun isAvailable(cart: Cart, current: LocalDateTime): Boolean =
        current.toLocalDate() <= expirationDate &&
                current.toLocalTime() in startHour..<endHour

    override fun discountPrice(cart: Cart): Int {
        return (cart.totalPrice * (discountRate / 100)).toInt()
    }
}