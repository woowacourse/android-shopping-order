package woowacourse.shopping.domain.cart

data class Cart(
    private val items: List<CartItem>,
    val shippingPrice: Int = DEFAULT_SHIPPING_PRICE,
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

    companion object {
        private const val DEFAULT_SHIPPING_PRICE = 3000
    }
}