package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import java.time.LocalDate
import java.time.LocalTime

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
