package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.Cart
import java.time.Instant
import java.util.Date

class BogoCondition(
    private val expirationDate: Date?,
    private val buyQuantity: Int?,
) : CouponCondition {
    override fun isValid(carts: List<Cart>): Boolean {
        expirationDate ?: return false
        buyQuantity ?: return false

        if (expirationDate.before(Date.from(Instant.now()))) return false
        val discountCart = findDiscountCart(carts, buyQuantity)
        return discountCart != null
    }

    private fun findDiscountCart(
        carts: List<Cart>,
        buyQuantity: Int,
    ): Cart? {
        return carts.filter { cart -> cart.quantity >= buyQuantity }
            .maxByOrNull { it.product.price }
    }
}
