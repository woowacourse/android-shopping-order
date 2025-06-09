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

    override fun getDiscountPrice(cartItems: List<CartItem>): Int =
        cartItems
            .filter { cartItem -> cartItem.quantity >= buyQuantity + getQuantity }
            .maxOf { cartItem -> cartItem.product.price }

    override fun getDiscountDeliveryFee(): Int = 0
}
