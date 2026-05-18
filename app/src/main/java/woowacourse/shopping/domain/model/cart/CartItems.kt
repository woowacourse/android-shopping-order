package woowacourse.shopping.domain.model.cart

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

    fun selectedCartItemsPrice(
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

    fun increase(cartId: Int): CartItems {
        val target = findById(cartId) ?: return this
        return replace(target, target.increaseQuantity())
    }

    fun decrease(cartId: Int): CartItems {
        val target = findById(cartId) ?: return this
        val decreased = target.decreaseQuantity()
        return if (decreased.quantity.isZero) {
            remove(cartId)
        } else {
            replace(target, decreased)
        }
    }

    fun remove(cartId: Int): CartItems = CartItems(values.filter { it.id != cartId })

    fun findQuantity(cartId: Int): Quantity = findById(cartId)?.quantity ?: Quantity.ZERO

    fun contains(cartId: Int): Boolean = findById(cartId) != null

    fun subList(
        fromIndex: Int,
        toIndex: Int,
    ): List<CartItem> {
        val safeFrom = fromIndex.coerceIn(0, values.size)
        val safeTo = toIndex.coerceIn(safeFrom, values.size)
        return values.subList(safeFrom, safeTo)
    }

    fun size(): Int = values.size

    private fun findById(cartId: Int): CartItem? = values.firstOrNull { it.id == cartId }

    private fun replace(
        target: CartItem,
        replacement: CartItem,
    ): CartItems = CartItems(values.map { if (it.isSameCartItem(target)) replacement else it })
}
