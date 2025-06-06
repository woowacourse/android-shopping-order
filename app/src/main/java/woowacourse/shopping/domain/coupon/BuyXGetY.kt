package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

data class BuyXGetY(
    override val description: String,
    override val code: String,
    override val explanationDate: LocalDate,
    override val id: Long,
    override val discountType: DiscountType = DiscountType.BUY_X_GET_Y,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon() {
    override fun isAvailable(
        shoppingCartProductToOrder: List<ShoppingCartProduct>,
        today: LocalDate,
        now: LocalTime,
    ): Boolean {
        if (!super.isAvailable(shoppingCartProductToOrder, today, now)) {
            return false
        }

        return shoppingCartProductToOrder.any {
            it.quantity >= buyQuantity + getQuantity
        }
    }

    override fun discountAmount(shoppingCartProductToOrder: List<ShoppingCartProduct>): Int {
        val target =
            shoppingCartProductToOrder.filter {
                it.quantity >= buyQuantity + getQuantity
            }.minBy {
                it.price
            }
        return target.price / target.quantity
    }
}
