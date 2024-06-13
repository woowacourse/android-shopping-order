package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.model.OrderInformation.Companion.SHIPPING_FEE

class DiscountCalculator(
    private val orderInformation: OrderInformation,
    coupons: List<Coupon>,
) {
    private var _coupons: List<Coupon> = coupons.map { it.copy() }
    private val coupons: List<Coupon> get() = _coupons.map { it.copy() }

    fun findAvailableCoupons(selectedCartItems: List<CartItem>) = coupons.filter { it.isAvailability(selectedCartItems) }

    fun calculateDiscountAmount(selectedCouponId: Long, isChecked: Boolean): Int {
        val selectedCoupon = coupons.find { it.id == selectedCouponId } ?: throw NoSuchElementException(INVALID_COUPON.format(selectedCouponId))
        return if (isChecked) {
            selectedCoupon.calculateDiscountAmount(orderInformation.cartItems)
        } else {
            NO_DISCOUNT
        }
    }

    fun calculateTotalAmount(
        selectedCouponId: Long,
        isChecked: Boolean,
    ): Int {
        val orderAmount = orderInformation.calculateDefaultTotalAmount()
        val discountAmount = calculateDiscountAmount(selectedCouponId, isChecked)
        return orderAmount + discountAmount
    }

    fun calculateShippingFee(selectedCouponId: Long, isChecked: Boolean): Int {
        val selectedCoupon = coupons.find { it.id == selectedCouponId } ?: throw NoSuchElementException(INVALID_COUPON.format(selectedCouponId))
        val isSelectedFreeShipping = selectedCoupon.discountType == FreeShippingCoupon.TYPE
        return if (isSelectedFreeShipping) orderInformation.determineShippingFee(isChecked) else SHIPPING_FEE
    }

    companion object {
        const val NO_DISCOUNT = 0
        const val INVALID_COUPON = "id가 %d인 쿠폰을 찾을 수 없습니다."
    }
}
