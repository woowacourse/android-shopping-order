package woowacourse.shopping.presentation.order

import woowacourse.shopping.domain.model.coupon.CouponContext

data class PaymentSummaryUiState(
    val orderPrice: Int,
    val couponDiscountPrice: Int = 0,
    val shippingFee: Int = 3000,
) {
    val totalPaymentAmount: Int get() = orderPrice + couponDiscountPrice + shippingFee

    fun applyCoupon(coupon: CouponContext): PaymentSummaryUiState =
        PaymentSummaryUiState(
            orderPrice,
            this.couponDiscountPrice - coupon.getDiscountPrice(),
            this.shippingFee - coupon.getDiscountDeliveryFee(),
        )

    fun cancelCoupon(coupon: CouponContext): PaymentSummaryUiState =
        PaymentSummaryUiState(
            orderPrice,
            this.couponDiscountPrice + coupon.getDiscountPrice(),
            this.shippingFee + coupon.getDiscountDeliveryFee(),
        )
}
