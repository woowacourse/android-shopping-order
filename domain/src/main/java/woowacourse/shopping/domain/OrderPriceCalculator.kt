package woowacourse.shopping.domain

object OrderPriceCalculator {

    fun calculateTotalOrderPrice(cartItems: Set<CartItem>): Int {
        return cartItems.sumOf { it.getOrderPrice() }
    }
}
