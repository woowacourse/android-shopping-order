package woowacourse.shopping.domain.entity.coupon

import woowacourse.shopping.domain.entity.Cart
import java.time.LocalDateTime

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val discountableMinPrice: Long,
    override val expirationDate: LocalDateTime,
    override val targetDateTime: LocalDateTime,
) : Coupon(id, code, description, discountableMinPrice, expirationDate, targetDateTime) {
    override fun available(cart: Cart): Boolean {
        return !isExpired && cart.totalPrice() >= discountableMinPrice
    }

    override fun calculateDiscount(
        cart: Cart,
        shippingFee: Long,
    ): DiscountResult {
        val totalPrice = cart.totalPrice()
        return DiscountResult(totalPrice, discountPrice = shippingFee, shippingFee = shippingFee)
    }
}
