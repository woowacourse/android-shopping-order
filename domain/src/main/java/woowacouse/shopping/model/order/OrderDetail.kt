package woowacouse.shopping.model.order

import woowacouse.shopping.model.cart.CartProduct
import java.time.LocalDate

data class OrderDetail(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderDateTime: String,
    val products: List<CartProduct>,
) {

    fun getOrderDate(): LocalDate {
        return LocalDate.parse(orderDateTime.substringBefore("T"))
    }

    fun getTotalPrice(): Int {
        return products.sumOf { it.product.price * it.count } - usedPoint
    }
}
