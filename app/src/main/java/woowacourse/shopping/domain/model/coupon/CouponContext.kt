package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

class CouponContext(
    val coupon: Coupon,
    val cartItems: List<CartItem>,
) {
    fun getDiscountPrice(): Int = coupon.getDiscountPrice(cartItems)

    fun getDiscountDeliveryFee(): Int = coupon.getDiscountDeliveryFee()
}
