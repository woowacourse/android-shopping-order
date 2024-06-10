package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartItem

class Order(coupons: List<Coupon>) {
    private var _coupons: List<Coupon> = coupons.map { it.copy() }
    private val coupons: List<Coupon> get() = _coupons.map { it.copy() }

    fun findAvailableCoupons(selectedCartItems: List<CartItem>) = coupons.filter { it.isAvailability(selectedCartItems) }

    fun calculateDiscountAmount(selectedCouponId: Long, cartItems: List<CartItem>, isChecked: Boolean): Int {
        val selectedCoupon = coupons.find { it.id == selectedCouponId } ?: throw NoSuchElementException(INVALID_COUPON.format(selectedCouponId))
        return if (isChecked) {
            selectedCoupon.calculateDiscountAmount(cartItems)
        } else {
            NO_DISCOUNT
        }
    }

    fun calculateTotalAmount(
        selectedCouponId: Long,
        cartItems: List<CartItem>,
        isChecked: Boolean,
    ): Int {
        val orderAmount = cartItems.sumOf { it.product.price * it.quantity }
        val discountAmount = calculateDiscountAmount(selectedCouponId, cartItems, isChecked)
        val shippingFee = if (discountAmount != -SHIPPING_FEE) {
            calculateDeliveryFee(selectedCouponId, isChecked)
        } else {
            SHIPPING_FEE
        }
        return orderAmount + discountAmount + shippingFee
    }

    fun calculateDeliveryFee(selectedCouponId: Long, isChecked: Boolean): Int {
        val selectedCoupon = coupons.find { it.id == selectedCouponId } ?: throw NoSuchElementException(INVALID_COUPON.format(selectedCouponId))
        return if (isChecked && selectedCoupon.discountType == CouponType.FREE_SHIPPING.discountType) {
                FREE_SHIPPING_FEE
            } else {
                SHIPPING_FEE
            }
    }

    companion object {
        const val SHIPPING_FEE = 3_000
        const val FREE_SHIPPING_FEE = 0
        const val NO_DISCOUNT = 0
        const val INVALID_COUPON = "id가 %d인 쿠폰을 찾을 수 없습니다."
    }
}
