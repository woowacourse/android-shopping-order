package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Coupon.Companion.INVALID_DISCOUNT_MESSAGE
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime

class BuyXgetYCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val buyQuantity: Int,
    private val getQuantity: Int,
) : Coupon {
    override fun available(
        cartItems: List<CartItem>,
        currentDateTime: LocalDateTime,
    ): Boolean {
        if (isExpired(currentDateTime.toLocalDate())) return false
        return cartItems.any { it.quantity.count >= buyQuantity + getQuantity }
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        if (!available(cartItems)) {
            throw IllegalArgumentException(INVALID_DISCOUNT_MESSAGE)
        }
        val maxPriceCartItem =
            cartItems
                .filter { it.quantity.count >= buyQuantity + getQuantity }
                .maxBy { it.product.price }
        return maxPriceCartItem.product.price * getQuantity
    }
}
