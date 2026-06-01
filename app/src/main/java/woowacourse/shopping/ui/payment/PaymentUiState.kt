package woowacourse.shopping.ui.payment

import woowacourse.shopping.core.formatter.toDiscountPriceString
import woowacourse.shopping.core.formatter.toPriceString
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.payment.Payment
import woowacourse.shopping.ui.payment.uimodel.CouponUiModel
import woowacourse.shopping.ui.payment.uimodel.PaymentUiModel
import woowacourse.shopping.ui.payment.uimodel.toUiModel

data class PaymentUiState(
    val coupons: List<CouponUiModel> = emptyList(),
    val selectedCouponCode: String? = null,
    val isLoading: Boolean = false,
    val isPaymentProcessing: Boolean = false,
    val payment: PaymentUiModel,
) {
    fun isSelected(coupon: CouponUiModel): Boolean = coupon.code == selectedCouponCode
}

fun Payment.toUiState(
    coupons: List<Coupon>,
    isLoading: Boolean,
    isPaymentProcessing: Boolean,
): PaymentUiState =
    PaymentUiState(
        coupons = coupons.map { it.toUiModel(enabled = canApply(it)) },
        selectedCouponCode = selectedCoupon?.code,
        isLoading = isLoading,
        isPaymentProcessing = isPaymentProcessing,
        payment = PaymentUiModel(
            formattedOrderAmount = orderAmount.toPriceString(),
            formattedCouponDiscountAmount = couponDiscountAmount.toDiscountPriceString(),
            formattedDeliveryFee = deliveryFee.toPriceString(),
            formattedTotalPaymentAmount = totalPaymentAmount.toPriceString(),
        ),
    )
