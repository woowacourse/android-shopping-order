package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderHistoryUIModel

fun List<OrderHistory>.toUIModel(): List<OrderHistoryUIModel> = map { it.toUIModel() }

fun OrderHistory.toUIModel(): OrderHistoryUIModel =
    OrderHistoryUIModel(
        id = id,
        orderItems = orderItems.map { it.toUIModel() },
        totalPrice = totalPrice,
        payPrice = payPrice,
        earnedPoints = earnedPoints,
        usedPoints = usedPoints,
        orderDate = orderDate
    )
