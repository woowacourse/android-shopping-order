package woowacourse.shopping.mapper

import woowacourse.shopping.data.dto.OrderHistoryDto
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.uimodel.OrderHistoryUIModel

fun OrderHistory.toUIModel() = OrderHistoryUIModel(
    id = id,
    orderItems = orderItems.map { it.toUIModel() },
    totalPrice = totalPrice,
    payPrice = payPrice,
    earnedPoints = earnedPoints,
    usedPoints = usedPoints,
    orderDate = orderDate,
)

fun OrderHistoryDto.toDomain() = OrderHistory(
    id = id,
    orderItems = orderItems.map { it.toDomain() },
    totalPrice = totalPrice,
    payPrice = payPrice,
    earnedPoints = earnedPoints,
    usedPoints = usedPoints,
    orderDate = orderDate,
)
