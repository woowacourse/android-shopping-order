package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.PaymentRequest
import woowacourse.shopping.data.dto.PaymentResponse
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.discount.Point

fun PaymentResponse.toPayment(): Payment = Payment(
    originalPrice = Price(originalPayment),
    finalPrice = Price(finalPayment),
    usedPoint = Point(point),
)

fun Payment.toPaymentRequest(): PaymentRequest = PaymentRequest(
    originalPayment = originalPrice.value,
    finalPayment = finalPrice.value,
    usedPoint = usedPoint.value,
)
