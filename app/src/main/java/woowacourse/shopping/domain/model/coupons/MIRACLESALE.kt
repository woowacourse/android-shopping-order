package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate
import java.time.LocalTime

data class MIRACLESALE(
    override val id: Long,
    override val code: String = "MIRACLESALE",
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val discount: Int,
    val availableTime: AvailableLocalTime,
) : Coupon() {
    data class AvailableLocalTime(
        val start: LocalTime,
        val end: LocalTime,
    )

    override fun calculateDiscountRate(carts: List<Cart>): Int {
        val totalPrice = carts.sumOf { it.totalPrice }
        val discountRate = discount / 100.0

        return (totalPrice * discountRate).toInt()
    }
}
