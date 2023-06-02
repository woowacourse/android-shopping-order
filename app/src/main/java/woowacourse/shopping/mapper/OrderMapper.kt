package woowacourse.shopping.mapper

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderUIModel

fun List<Order>.toUIModel(): List<OrderUIModel> = map { it.toUIModel() }

fun Order.toUIModel(): OrderUIModel =
    OrderUIModel(
        id = id,
        orderItems = orderItems.map { it.toUIModel() },
        totalPrice = totalPrice,
        payPrice = payPrice,
        earnedPoints = earnedPoints,
        usedPoints = usedPoints,
        orderDate = orderDate
    )
