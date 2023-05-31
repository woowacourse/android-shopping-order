package woowacourse.shopping.mapper

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.model.UiOrder

fun UiOrder.toDomain(): Order = Order(
    orderProducts = orderProducts.toDomain(),
    totalPayment = totalPayment.toDomain(),
)

fun Order.toUi(): UiOrder = UiOrder(
    orderProducts = orderProducts.toUi(),
    totalPayment = totalPayment.toUi(),
)
