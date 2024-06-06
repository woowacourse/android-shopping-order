package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart
import java.time.Instant
import java.util.Date

class Bogo(override val coupon: Coupon) : CouponState() {
    private lateinit var discountCart: Cart

    override fun createState(coupon: Coupon): CouponState = Bogo(coupon)

    override fun isValidCoupon(carts: List<Cart>): Boolean {
        if (coupon.expirationDate < Date.from(Instant.now())) return false
        val buyQuantity = coupon.buyQuantity ?: return false
        discountCart = findDiscountCart(carts, buyQuantity) ?: return false
        return true
    }

    private fun findDiscountCart(
        carts: List<Cart>,
        buyQuantity: Int,
    ): Cart? =
        carts.filter { cart -> buyQuantity <= cart.quantity }
            .maxByOrNull { it.product.price }

    override fun calculateDiscount(totalAmount: Int): Int {
        val getQuantity = coupon.getQuantity ?: return 0

        val price = discountCart.product.price
        val discountCartQuantity = discountCart.quantity

        return (discountCartQuantity - getQuantity) * price
    }
}
