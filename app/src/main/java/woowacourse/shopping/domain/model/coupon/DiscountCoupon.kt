package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalDate
import java.time.LocalTime

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
