package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

data class CouponFixedDiscount(
    override val couponBase: CouponBase,
    val discountPrice: Int,
    val minimumOrderPrice: Int,
    val couponDiscountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean =
        !isExpired() && minimumOrderPrice <= cartItems.sumOf { cartItem -> cartItem.totalPrice }

    override fun getDiscountPrice(cartItems: List<CartItem>): Int = discountPrice

    override fun getDiscountDeliveryFee(): Int = 0
}
