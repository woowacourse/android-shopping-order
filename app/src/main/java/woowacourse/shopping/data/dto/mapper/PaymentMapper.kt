package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.PaymentRequest
import woowacourse.shopping.data.dto.PaymentResponse
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.model.Price

fun PaymentResponse.toPayment(): Payment = Payment(
    originalPayment = Price(originalPayment),
    finalPayment = Price(finalPayment),
    usedPoint = Point(usedPoint),
)

fun Payment.toPaymentRequest(): PaymentRequest = PaymentRequest(
    originalPayment = originalPayment.value,
    finalPayment = finalPayment.value,
    usedPoint = usedPoint.value,
)
