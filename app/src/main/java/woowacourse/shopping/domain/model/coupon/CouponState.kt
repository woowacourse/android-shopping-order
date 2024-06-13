package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

sealed class CouponState(open val coupon: Coupon) {
    abstract fun isValid(cartItems: List<CartItem>): Boolean

    abstract fun discountPrice(cartItems: List<CartItem>): Int

    companion object {
        fun makeCouponState(coupon: Coupon): CouponState {
            return when (coupon.discountType) {
                DiscountType.FIXED -> FixedCoupon(coupon)
                DiscountType.BUYXGETY -> BuyXgetYCoupon(coupon)
                DiscountType.FREESHIPPING -> FreeShippingCoupon(coupon)
                DiscountType.PERCENTAGE -> PercentageCoupon(coupon)
            }
        }
    }
}
