package woowacourse.shopping.domain.model

import java.lang.IllegalArgumentException
import java.time.LocalDate

class BuyXgetYCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    private val buyQuantity: Int,
    private val getQuantity: Int,
) : Coupon {
    override fun available(cartItems: List<CartItem>): Boolean {
        return cartItems.any { it.quantity.count >= buyQuantity + getQuantity }
    }

    override fun discountPrice(cartItems: List<CartItem>): Int {
        if (available(cartItems)) {
            throw IllegalArgumentException(INVALID_DISCOUNT)
        }

        val maxPriceCartItem =
            cartItems
                .filter { it.quantity.count >= buyQuantity + getQuantity }
                .maxBy { it.product.price }
        return maxPriceCartItem.product.price * getQuantity
    }

    companion object {
        private const val INVALID_DISCOUNT = "적용할 수 없는 쿠폰입니다."
    }
}
