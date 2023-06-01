package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.model.UiPayment

fun UiPayment.toDomain(): Payment = Payment(
    originalPayment = originalPayment.toDomain(),
    finalPayment = finalPayment.toDomain(),
    usedPoint = usedPoint.toDomain(),
)

fun Payment.toUi(): UiPayment = UiPayment(
    originalPayment = originalPayment.toUi(),
    finalPayment = finalPayment.toUi(),
    usedPoint = usedPoint.toUi(),
)
