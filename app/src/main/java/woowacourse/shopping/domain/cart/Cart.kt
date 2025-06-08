package woowacourse.shopping.domain.cart

@JvmInline
value class Cart(
    private val items: List<CartItem>
) {
    val totalPrice: Int get() = items.sumOf { it.price }

    fun hasEnoughItems(thresholdQuantity: Int): Boolean {
        return items.any { it.quantity >= thresholdQuantity }
    }

    fun findMostExpensiveItemPrice(thresholdQuantity: Int): Int {
        return items
            .asSequence()
            .filter { cartItem: CartItem ->
                cartItem.quantity >= thresholdQuantity
            }.maxBy {
                it.price
            }.productPrice
    }
}