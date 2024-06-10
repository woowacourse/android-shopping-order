package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart
import java.time.LocalDateTime

data class FixedCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDateTime,
    val discountableMinPrice: Long,
    val discount: Long,
) : Coupon(id, code, description, expirationDate) {
    override fun available(
        cart: Cart,
        targetDateTime: LocalDateTime,
    ): Boolean {
        return !isExpired(targetDateTime) && cart.totalPrice() >= discountableMinPrice
    }

    override fun calculateDiscount(
        cart: Cart,
        shippingFee: Long,
    ): DiscountResult {
        val totalPrice = cart.totalPrice()
        return DiscountResult(totalPrice, discount, shippingFee)
    }
}
