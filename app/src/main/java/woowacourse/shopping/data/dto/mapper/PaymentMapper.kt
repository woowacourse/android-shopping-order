package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.PaymentRequest
import woowacourse.shopping.data.dto.PaymentResponse
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.model.Price

fun PaymentResponse.toPayment(): Payment = Payment(
    originalPrice = Price(originalPayment),
    finalPrice = Price(finalPayment),
    usedPoint = Point(usedPoint),
)

fun Payment.toPaymentRequest(): PaymentRequest = PaymentRequest(
    originalPayment = originalPrice.value,
    finalPayment = finalPrice.value,
    usedPoint = usedPoint.value,
)
