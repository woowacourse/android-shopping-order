package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.domain.Order

fun DataOrder.toDomain(): Order =
    Order(
        orderId = orderId,
        createdAt = createdAt,
        orderItems = orderItems.map { it.toDomain() },
        totalPrice = totalPrice.toDomain(),
        usedPoint = usedPoint.toDomain(),
        earnedPoint = earnedPoint.toDomain()
    )

fun Order.toData(): DataOrder =
    DataOrder(
        orderId = orderId,
        createdAt = createdAt,
        orderItems = orderItems.map { it.toData() },
        totalPrice = totalPrice.toData(),
        usedPoint = usedPoint.toData(),
        earnedPoint = earnedPoint.toData()
    )
