package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Coupon.Companion.INVALID_DISCOUNT_MESSAGE
import java.lang.IllegalArgumentException
import java.time.LocalDate

class FixedCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val discount: Int,
    val minimumPrice: Int,
) : Coupon {
    override fun available(cartItems: List<CartItem>): Boolean {
        if (isExpired()) return false
        return totalOrderPrice(cartItems) >= minimumPrice
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        if (!available(cartItems)) {
            throw IllegalArgumentException(INVALID_DISCOUNT_MESSAGE)
        }
        return discount
    }
}
