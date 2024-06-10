
package woowacourse.shopping.domain.model

import java.time.LocalDate

data class BoGoCoupon(
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon() {
    override fun isSatisfiedPolicy(orders: Orders): Boolean {
        val orderItems = orders.orderItems
        return orderItems.any { it.quantity > buyQuantity }
    }

    override fun discountAmount(orders: Orders): Int {
        val orderItems = orders.orderItems
        val overBuyQuantityCartItems = orderItems.filter { it.quantity > buyQuantity }
        val mostExpensiveCartItem = overBuyQuantityCartItems.maxBy { it.product.price }

        return getQuantity * mostExpensiveCartItem.product.price
    }
}
