package woowacourse.shopping.domain.cart

data class Cart(val value: List<CartItem>) {
    fun calculateTotalPrice(): Int = value.sumOf { it.calculateOrderPrice() }

    fun updateElement(from: CartItem, to: CartItem): Cart {
        val index = value.indexOf(from)
        if (index == -1) return this
        val newList = value.toMutableList().apply {
            this[index] = to
        }
        return Cart(newList)
    }
}
