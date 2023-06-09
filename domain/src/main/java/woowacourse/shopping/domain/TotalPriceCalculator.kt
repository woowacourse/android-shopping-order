package woowacourse.shopping.domain

object TotalPriceCalculator {

    fun calculate(cartItems: Set<CartItem>): Int {
        return cartItems.sumOf { it.getOrderPrice() }
    }
}
