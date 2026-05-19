package woowacourse.shopping.domain.model

data class Cart(
    private val _items: List<CartItem> = emptyList(),
) {
    val items = _items.toList()
    val size: Int get() = items.size

    operator fun get(productId: Long): CartItem? = items.find { it.product.id == productId }

    val totalPrice: Money get() = Money(items.sumOf { it.getTotalPrice().amount })

    val totalQuantity: Int get() = items.sumOf { it.quantity }
}
