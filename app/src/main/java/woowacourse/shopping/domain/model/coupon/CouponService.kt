package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartsPrice

class CouponService {
    fun isValid(
        coupon: Coupon,
        cartItems: List<CartItem>,
    ): Boolean =
        when (coupon) {
            is Coupon.Fixed ->
                isValidPeriod(coupon) && isOverMinimumAmount(cartItems, coupon)

            is Coupon.BonusGoods ->
                isValidPeriod(coupon) &&
                    cartItems.any { it.quantity >= coupon.calculateBonusGoods.buyQuantity }

            is Coupon.FreeShipping ->
                isValidPeriod(coupon) && isOverMinimumAmount(cartItems, coupon)

            is Coupon.Percentage -> isValidPeriod(coupon)
        }

    private fun isValidPeriod(coupon: Coupon): Boolean = coupon.expirationPeriod.isValidTime

    private fun isOverMinimumAmount(
        cartItems: List<CartItem>,
        coupon: Coupon,
    ): Boolean = cartItems.sumOf { it.totalPrice } >= coupon.minimumAmount.amount

    fun applyCoupon(
        coupon: Coupon,
        originalCartPrice: CartsPrice,
        cartItems: List<CartItem>,
    ): CartsPrice {
        require(isValid(coupon, cartItems)) { "유효하지 않은 쿠폰입니다: ${coupon.code}" }

        return when (coupon) {
            is Coupon.Fixed ->
                originalCartPrice.copy(
                    totalPrice = coupon.discountedAmount.applyDiscount(originalCartPrice.totalPrice),
                )

            is Coupon.BonusGoods -> {
                val discountedAmount = coupon.calculateBonusGoods.getDiscountedPrice(cartItems)

                originalCartPrice.copy(
                    totalPrice = discountedAmount.applyDiscount(originalCartPrice.totalPrice),
                )
            }

            is Coupon.FreeShipping -> originalCartPrice.copy(shippingFee = 0)

            is Coupon.Percentage ->
                originalCartPrice.copy(
                    totalPrice = coupon.discountedAmount.applyDiscount(originalCartPrice.totalPrice),
                )
        }
    }
}
