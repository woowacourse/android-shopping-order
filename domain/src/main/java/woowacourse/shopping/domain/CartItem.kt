package woowacourse.shopping.domain

import java.time.LocalDateTime

class CartItem(
    val id: Long,
    val product: Product,
    val addedTime: LocalDateTime,
    count: Int
) {
    var count: Int = count
        private set

    fun getOrderPrice(): Int {
        return product.price * count
    }

    fun plusCount() {
        count++
    }

    fun minusCount() {
        if (count > 1) count--
    }

    override fun equals(other: Any?): Boolean = if (other is CartItem) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
