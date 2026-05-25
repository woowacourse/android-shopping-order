package woowacourse.shopping.ui.payment.uimodel

import woowacourse.shopping.core.formatter.toDiscountPriceString
import woowacourse.shopping.core.formatter.toPriceString
import woowacourse.shopping.domain.model.payment.Payment

data class PaymentUiModel(
    val formattedOrderAmount: String,
    val formattedCouponDiscountAmount: String,
    val formattedDeliveryFee: String,
    val formattedTotalPaymentAmount: String,
)

fun Payment.toUiModel(): PaymentUiModel =
    PaymentUiModel(
        formattedOrderAmount = orderAmount.toPriceString(),
        formattedCouponDiscountAmount =
            couponDiscountAmount.toDiscountPriceString(),
        formattedDeliveryFee = deliveryFee.toPriceString(),
        formattedTotalPaymentAmount =
            totalPaymentAmount.toPriceString(),
    )
