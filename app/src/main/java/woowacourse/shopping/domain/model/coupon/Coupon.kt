package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.policy.DiscountPolicy
import java.time.LocalDate

class Coupon(
    val id: Int,
    val description: String,
    val expirationDate: LocalDate,
    val minimumPrice: Int?,
    private val discountPolicy: DiscountPolicy,
) {
    fun available(cartItems: List<CartItem>): Boolean {
        return discountPolicy.available(this, cartItems)
    }

    fun discountPrice(cartItems: List<CartItem>): Int {
        return discountPolicy.discountPrice(cartItems)
    }

    companion object {
        const val DELIVERY_FEE = 3000
    }
}
