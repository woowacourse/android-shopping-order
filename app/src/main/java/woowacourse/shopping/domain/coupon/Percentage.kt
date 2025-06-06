package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

data class Percentage(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDate,
    override val id: Long,
    override val discountType: DiscountType = DiscountType.PERCENTAGE,
    val discount: Int,
    override val availableStartTime: LocalTime? = null,
    override val availableEndTime: LocalTime? = null,
) : Coupon() {
    override fun disCountAmount(shoppingCartProductToOrder: List<ShoppingCartProduct>): Int {
        val totalPrice = shoppingCartProductToOrder.sumOf { it.price }
        return totalPrice * (discount / 100)
    }
}
