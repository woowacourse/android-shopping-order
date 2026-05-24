package woowacourse.shopping.model.cart

import woowacourse.shopping.model.cart.CartItem

data class Cart(
    val items: List<CartItem>,
) {
    fun priceOf(selectedIds: Set<Long>): Long =
        items
            .filter { it.id in selectedIds }
            .sumOf { it.totalPrice.value }

    fun totalQuantityOf(selectedIds: Set<Long>): Int =
        items
            .filter { it.id in selectedIds }
            .sumOf { it.quantity }

    fun findByProductId(productId: Long): CartItem? = items.find { it.product.id == productId }
}
