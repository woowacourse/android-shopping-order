package woowacourse.shopping.presentation.common.model

import woowacourse.shopping.domain.model.PaymentSummary

data class PaymentSummaryUiModel(
    val orderPrice: Int,
    val discountPrice: Int,
    val deliveryFee: Int,
    val finalPaymentPrice: Int,
)

fun PaymentSummary.toUiModel(): PaymentSummaryUiModel =
    PaymentSummaryUiModel(
        orderPrice = orderPrice,
        discountPrice = discountPrice,
        deliveryFee = deliveryFee,
        finalPaymentPrice = finalPaymentPrice,
    )
