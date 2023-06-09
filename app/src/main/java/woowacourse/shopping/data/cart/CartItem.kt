package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.Product

data class CartItem(
    val id: Long,
    val product: Product,
    val quantity: Int
) {
    val price = product.price * quantity

    override fun equals(other: Any?): Boolean = if (other is CartItem) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
