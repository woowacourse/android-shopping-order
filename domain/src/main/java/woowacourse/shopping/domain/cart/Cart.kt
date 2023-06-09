package woowacourse.shopping.domain.cart

data class Cart(val value: Set<CartItem>) {
    fun calculateTotalPrice(): Int = value.sumOf { it.calculateOrderPrice() }
}
