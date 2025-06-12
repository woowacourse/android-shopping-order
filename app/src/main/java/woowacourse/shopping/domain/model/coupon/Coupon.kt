package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val code: String

    abstract val description: String

    abstract val expirationDate: LocalDate

    abstract val id: Long

    abstract fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean
}

data class BogoCoupon(
    val buyQuantity: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val getQuantity: Int,
    override val id: Long,
) : Coupon() {
    override fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean = carts.any { it.quantity >= 3 }
}

data class DiscountCoupon(
    override val code: String,
    override val description: String,
    val discount: Int,
    override val expirationDate: LocalDate,
    override val id: Long,
    val minimumAmount: Int,
) : Coupon() {
    override fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean = carts.sumOf { it.totalPrice } >= minimumAmount
}

data class FreeShippingCoupon(
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    override val id: Long,
    val minimumAmount: Int,
) : Coupon() {
    override fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean = carts.sumOf { it.totalPrice } >= minimumAmount
}

data class TimeLimitedCoupon(
    val availableTime: AvailableTime,
    override val code: String,
    override val description: String,
    val discount: Int,
    override val expirationDate: LocalDate,
    override val id: Long,
) : Coupon() {
    override fun isApplicable(
        carts: List<CartProduct>,
        time: LocalTime,
    ): Boolean = availableTime.isAvailable(time)
}
