package woowacourse.shopping.domain.model.coupons

import woowacourse.shopping.domain.model.Cart
import java.time.LocalDate

data class FIXED5000(
    override val id: Long,
    override val code: String = "FIXED5000",
    override val description: String,
    override val expirationDate: LocalDate,
    override val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon() {
    override fun calculateDiscountRate(carts: List<Cart>): Int {
        val totalPrice = carts.sumOf { it.totalPrice }

        return totalPrice - discount
    }
}
