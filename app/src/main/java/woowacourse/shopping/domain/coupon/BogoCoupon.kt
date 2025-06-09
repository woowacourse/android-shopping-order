package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.ShoppingCarts
import java.time.LocalDate

data class BogoCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    fun isUsable(
        standardDate: LocalDate,
        order: ShoppingCarts,
    ): Boolean {
        if (isExpired(standardDate)) return false
        if (hasEnoughQuantity(order).not()) return false
        return true
    }

    private fun hasEnoughQuantity(order: ShoppingCarts): Boolean {
        val quantities = order.shoppingCarts.map { it.quantity }
        return quantities.any { it.value >= buyQuantity + getQuantity }
    }
}
