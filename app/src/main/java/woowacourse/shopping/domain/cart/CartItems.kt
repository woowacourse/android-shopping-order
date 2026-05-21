package woowacourse.shopping.domain.cart

data class CartItems(
    val values: List<CartItem> = emptyList(),
    val isLast: Boolean = false,
    val isFirst: Boolean = true,
    val totalPages: Int = 1,
) {
    val totalQuantity: Int
        get() = values.sumOf { it.quantity.value }

    val totalPrice: Int
        get() = values.sumOf { it.totalPrice }

    fun calculatePrice(targetIds: Set<Int>): Int = values.filter { targetIds.contains(it.id) }.sumOf { it.totalPrice }

    fun calculateQuantity(targetIds: Set<Int>): Int = values.filter { targetIds.contains(it.id) }.sumOf { it.quantity.value }

    fun getQuantityByProductId(productId: Int): Quantity = findByProductId(productId)?.quantity ?: Quantity.ZERO

    fun getQuantityByCartId(cartId: Int): Quantity = getCartItemByCartId(cartId)?.quantity ?: Quantity.ZERO

    fun contains(productId: Int): Boolean = findByProductId(productId) != null

    fun subList(
        fromIndex: Int,
        toIndex: Int,
    ): CartItems {
        val safeFrom = fromIndex.coerceIn(0, values.size)
        val safeTo = toIndex.coerceIn(safeFrom, values.size)
        return CartItems(
            values = values.subList(safeFrom, safeTo),
            isLast = safeTo == values.size,
            isFirst = safeFrom == 0,
            totalPages = totalPages,
        )
    }

    fun size(): Int = values.size

    fun findByProductId(productId: Int): CartItem? = values.firstOrNull { it.isSameProduct(productId) }

    fun getCartItemByCartId(cartId: Int): CartItem? = values.firstOrNull { it.id == cartId }

    private fun replace(
        target: CartItem,
        replacement: CartItem,
    ): CartItems = CartItems(values.map { if (it.isSameCartItem(target)) replacement else it })
}
