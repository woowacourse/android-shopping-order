package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.model.PaymentModel

fun PaymentModel.toDomain(): Payment = Payment(
    originalPrice = originalPayment.toDomain(),
    finalPrice = finalPayment.toDomain(),
    usedPoint = usedPoint.toDomain(),
)

fun Payment.toUi(): PaymentModel = PaymentModel(
    originalPayment = originalPrice.toUi(),
    finalPayment = finalPrice.toUi(),
    usedPoint = usedPoint.toUi(),
)
