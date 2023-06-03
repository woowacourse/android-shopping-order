package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Order
import woowacourse.shopping.ui.model.UiOrder

fun UiOrder.toDomain(): Order =
    Order(
        orderId = orderId,
        createdAt = createdAt,
        orderItems = orderItems.map { it.toDomain() },
        totalPrice = totalPrice.toDomain(),
        usedPoint = usedPoint.toDomain(),
        earnedPoint = earnedPoint.toDomain()
    )

fun Order.toUi(): UiOrder =
    UiOrder(
        orderId = orderId,
        createdAt = createdAt,
        orderItems = orderItems.map { it.toUi() },
        totalPrice = totalPrice.toUi(),
        usedPoint = usedPoint.toUi(),
        earnedPoint = earnedPoint.toUi()
    )
