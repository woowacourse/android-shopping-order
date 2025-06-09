package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

data class CouponFreeShipping(
    override val couponBase: CouponBase,
    val minimumOrderPrice: Int,
    val discountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean =
        !isExpired() && minimumOrderPrice <= cartItems.sumOf { cartItem -> cartItem.totalPrice }

    override fun getDiscountPrice(cartItems: List<CartItem>): Int = 0

    override fun getDiscountDeliveryFee(): Int = DEFAULT_DELIVERY_FEE

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3_000
    }
}
