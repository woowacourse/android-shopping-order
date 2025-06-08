package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon

data class PaymentDetail(
    private val selectedProducts: List<CartProduct>,
    val couponDiscount: Int = DEFAULT_COUPON_DISCOUNT,
    val deliveryFee: Int = DEFAULT_DELIVERY_FEE,
) {
    val orderAmount: Int get() = selectedProducts.sumOf { it.totalPrice }
    val totalPayment: Int get() = orderAmount - couponDiscount + deliveryFee

    fun discountByCoupon(coupon: Coupon): PaymentDetail {
        val discountAmount = coupon.calculateDiscountAmount(selectedProducts)

        val newDeliveryFee = if (coupon is FreeShippingCoupon) 0 else DEFAULT_DELIVERY_FEE

        return copy(
            couponDiscount = discountAmount,
            deliveryFee = newDeliveryFee,
        )
    }

    companion object {
        private const val DEFAULT_COUPON_DISCOUNT = 0
        private const val DEFAULT_DELIVERY_FEE = 3000
    }
}
