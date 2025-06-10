package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

data class Percentage(
    override val description: String,
    override val code: String,
    override val expirationDate: LocalDate,
    override val id: Long,
    val discount: Int,
    override val availableStartTime: LocalTime? = null,
    override val availableEndTime: LocalTime? = null,
) : Coupon() {
    override fun discountAmount(shoppingCartProductToOrder: List<ShoppingCartProduct>): Int {
        val totalPrice = shoppingCartProductToOrder.sumOf { it.price }.toBigDecimal()
        val discount = discount.toBigDecimal()
        val discountRate = discount.divide(100.toBigDecimal())
        return (totalPrice.multiply(discountRate)).toInt()
    }
}
