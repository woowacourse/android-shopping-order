package woowacourse.shopping.domain.cart

class CartItems(
    val values: List<CartItem> = emptyList(),
    val isLast: Boolean = false,
    val isFirst: Boolean = true,
    val totalPages: Int = 1,
) {
    val totalQuantity: Int
        get() = values.sumOf { it.quantity.value }

    val totalPrice: Int
        get() = values.sumOf { it.totalPrice }

    fun calculatePrice(
        targetIds: Set<Int>,
        isAll: Boolean,
    ): Int {
        if (isAll) return totalPrice
        return values.filter { targetIds.contains(it.id) }.sumOf { it.totalPrice }
    }

    fun calculateQuantity(
        targetIds: Set<Int>,
        isAll: Boolean,
    ): Int {
        if (isAll) return totalQuantity
        return values.filter { targetIds.contains(it.id) }.sumOf { it.quantity.value }
    }

    fun increase(productId: Int): CartItems {
        val target = findByProductId(productId) ?: return this
        return replace(target, target.increaseQuantity())
    }

    fun decrease(productId: Int): CartItems {
        val target = findByProductId(productId) ?: return this
        val decreased = target.decreaseQuantity()
        return if (decreased.quantity.isZero) {
            remove(productId)
        } else {
            replace(target, decreased)
        }
    }

    fun remove(productId: Int): CartItems = CartItems(values.filter { !it.isSameProduct(productId) })

    fun findQuantity(productId: Int): Quantity = findByProductId(productId)?.quantity ?: Quantity.ZERO

    fun contains(productId: Int): Boolean = findByProductId(productId) != null

    fun subList(
        fromIndex: Int,
        toIndex: Int,
    ): List<CartItem> {
        val safeFrom = fromIndex.coerceIn(0, values.size)
        val safeTo = toIndex.coerceIn(safeFrom, values.size)
        return values.subList(safeFrom, safeTo)
    }

    fun size(): Int = values.size

    fun findByProductId(productId: Int): CartItem? = values.firstOrNull { it.isSameProduct(productId) }

    private fun replace(
        target: CartItem,
        replacement: CartItem,
    ): CartItems = CartItems(values.map { if (it.isSameCartItem(target)) replacement else it })
}
