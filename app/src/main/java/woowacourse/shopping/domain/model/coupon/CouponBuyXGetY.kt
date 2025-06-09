package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

data class CouponBuyXGetY(
    override val couponBase: CouponBase,
    val buyQuantity: Int,
    val getQuantity: Int,
    val discountType: CouponDiscountType,
) : Coupon() {
    override fun isAvailable(cartItems: List<CartItem>): Boolean =
        !isExpired() &&
            cartItems.any { cartItem -> cartItem.quantity >= buyQuantity + getQuantity }

    fun getDiscountedCartItem(cartItems: List<CartItem>): CartItem =
        cartItems
            .filter { cartItem -> cartItem.quantity >= buyQuantity + getQuantity }
            .maxBy { cartItem -> cartItem.totalPrice }
}
