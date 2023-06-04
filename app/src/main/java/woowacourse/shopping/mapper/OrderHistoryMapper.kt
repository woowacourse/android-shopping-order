package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.uimodel.OrderHistoryUIModel
import java.time.LocalDateTime

fun OrderHistory.toUIModel() = OrderHistoryUIModel(
    id = id,
    orderItems = orderItems.map { it.toUIModel() },
    totalPrice = totalPrice,
    payPrice = payPrice,
    earnedPoints = earnedPoints,
    usedPoints = usedPoints,
    orderDate = LocalDateTime.parse(orderDate),
)
