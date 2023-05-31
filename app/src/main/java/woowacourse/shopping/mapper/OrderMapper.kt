package woowacourse.shopping.mapper

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.model.UiOrder

fun UiOrder.toDomain(): Order = Order(
    id = id,
    orderProducts = orderProducts.toDomain(),
    totalPayment = totalPayment.toDomain(),
)

fun Order.toUi(): UiOrder = UiOrder(
    id = id,
    orderProducts = orderProducts.toUi(),
    totalPayment = totalPayment.toUi(),
)

fun List<Order>.toUi(): List<UiOrder> = map { it.toUi() }
