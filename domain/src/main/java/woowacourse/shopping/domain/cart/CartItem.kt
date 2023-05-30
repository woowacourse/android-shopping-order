package woowacourse.shopping.domain.cart

import woowacourse.shopping.domain.product.Product
import java.time.LocalDateTime
import kotlin.math.max

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: Product,
    val addedTime: LocalDateTime
) {
    fun calculateOrderPrice(): Int = product.price * quantity

    fun plusQuantity(): CartItem = copy(quantity = quantity + 1)

    fun minusQuantity(): CartItem = copy(quantity = max(0, quantity - 1))

    override fun equals(other: Any?): Boolean = if (other is CartItem) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
