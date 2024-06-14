package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon

class BogoDiscountStrategy(private val buyQuantity: Int, private val getQuantity: Int) : DiscountStrategy {
    override fun applyDiscount(
        totalPrice: Int,
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Int {
        val totalItemCount = cartItems.sumOf { it.product.cartItemCounter.itemCount }

        return if (totalItemCount >= buyQuantity + getQuantity) {
            val discount = (totalPrice / (buyQuantity + getQuantity)) * getQuantity
            totalPrice - discount
        } else {
            totalPrice
        }
    }
}
