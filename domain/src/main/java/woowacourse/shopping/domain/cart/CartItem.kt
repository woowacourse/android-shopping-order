package woowacourse.shopping.domain.cart

import woowacourse.shopping.domain.product.Product
import kotlin.math.max

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: Product
) {
    fun calculateOrderPrice(): Int = product.price * quantity

    fun plusQuantity(): CartItem = copy(quantity = quantity + 1)

    fun minusQuantity(): CartItem = copy(quantity = max(0, quantity - 1))
}
