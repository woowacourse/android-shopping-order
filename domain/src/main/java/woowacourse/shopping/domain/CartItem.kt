package woowacourse.shopping.domain

import java.time.LocalDateTime
import kotlin.math.max

data class CartItem(
    val id: Long,
    val product: Product,
    val addedTime: LocalDateTime,
    val count: Int
) {
    fun calculateOrderPrice(): Int = product.price * count

    fun plusCount(): CartItem = copy(count = count + 1)

    fun minusCount(): CartItem = copy(count = max(0, count - 1))

    override fun equals(other: Any?): Boolean = if (other is CartItem) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
