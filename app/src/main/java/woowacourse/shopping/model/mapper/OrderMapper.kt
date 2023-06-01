package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.model.UiOrder

fun UiOrder.toDomain(): Order = Order(
    id = id,
    orderProducts = orderProducts.toDomain(),
    payment = payment.toDomain(),
)

fun Order.toUi(): UiOrder = UiOrder(
    id = id,
    orderProducts = orderProducts.toUi(),
    payment = payment.toUi(),
)

fun List<Order>.toUi(): List<UiOrder> = map { it.toUi() }
