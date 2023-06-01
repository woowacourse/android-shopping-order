package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.model.PaymentModel

fun PaymentModel.toDomain(): Payment = Payment(
    originalPayment = originalPayment.toDomain(),
    finalPayment = finalPayment.toDomain(),
    usedPoint = usedPoint.toDomain(),
)

fun Payment.toUi(): PaymentModel = PaymentModel(
    originalPayment = originalPayment.toUi(),
    finalPayment = finalPayment.toUi(),
    usedPoint = usedPoint.toUi(),
)
