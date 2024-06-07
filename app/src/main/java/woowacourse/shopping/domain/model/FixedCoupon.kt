package woowacourse.shopping.domain.model

import java.lang.IllegalArgumentException
import java.time.LocalDate

class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val discount: Int,
    private val minimumAmount: Int,
) : Coupon {
    override fun available(cartItems: List<CartItem>): Boolean {
        return totalOrderPrice(cartItems) >= minimumAmount
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        if (available(cartItems)) {
            throw IllegalArgumentException(INVALID_DISCOUNT)
        }
        return discount
    }

    private fun totalOrderPrice(cartItems: List<CartItem>): Int {
        return cartItems.sumOf { it.totalPrice() }
    }

    companion object {
        private const val INVALID_DISCOUNT = "적용할 수 없는 쿠폰입니다."
    }
}
