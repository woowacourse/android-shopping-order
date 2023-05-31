package woowacouse.shopping.model.cart

import woowacouse.shopping.model.product.Product

data class CartProduct(
    val id: Long,
    val product: Product,
    val count: Int,
    val checked: Boolean
) {
    fun increaseCount(): CartProduct = copy(count = (count + 1).coerceAtMost(MAX_COUNT))

    fun decreaseCount(): CartProduct = copy(count = (count - 1).coerceAtLeast(MIN_COUNT))

    fun updateCount(count: Int): CartProduct {
        if (count < 0)
            return this
        return copy(count = count)
    }

    fun updateCartIdByProductId(cartId: Long, productId: Long): CartProduct {
        if (productId == product.id)
            return copy(id = cartId)
        return this
    }

    companion object {
        private const val MIN_COUNT = 0
        private const val MAX_COUNT = 99
    }
}
