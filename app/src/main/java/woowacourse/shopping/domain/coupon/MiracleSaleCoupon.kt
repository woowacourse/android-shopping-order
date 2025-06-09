package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Receipt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MiracleSaleCoupon(
    override val description: String,
    override val expirationDate: LocalDate,
    val startHour: LocalTime,
    val endHour: LocalTime,
    private val discountRate: Double,
) : Coupon {
    override fun isAvailable(receipt: Receipt, current: LocalDateTime): Boolean =
        current.toLocalDate() <= expirationDate &&
                current.toLocalTime() in startHour..<endHour

    override fun discountPrice(receipt: Receipt): Int {
        return (receipt.totalPrice * (discountRate / 100)).toInt()
    }
}