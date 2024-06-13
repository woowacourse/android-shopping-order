package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import java.time.Instant
import java.util.Date

class Bogo(override val coupon: Coupon) : CouponState() {
    private lateinit var discountCart: CartItem

    override fun createState(coupon: Coupon): CouponState = Bogo(coupon)

    override fun isValidCoupon(carts: List<CartItem>): Boolean {
        if (coupon.expirationDate < Date.from(Instant.now())) return false
        val buyQuantity = coupon.buyQuantity ?: return false
        discountCart = findDiscountCart(carts, buyQuantity) ?: return false
        return true
    }

    private fun findDiscountCart(
        carts: List<CartItem>,
        buyQuantity: Int,
    ): CartItem? =
        carts.filter { cart -> buyQuantity <= cart.quantity }
            .maxByOrNull { it.price }

    override fun calculateDiscount(totalAmount: Int): Int {
        val getQuantity = coupon.getQuantity ?: return 0

        val price = discountCart.price
        val discountCartQuantity = discountCart.quantity

        return (discountCartQuantity - getQuantity) * price.toInt() * (-1)
    }
}
