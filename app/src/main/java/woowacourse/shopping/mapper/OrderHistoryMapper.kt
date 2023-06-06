package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderHistories
import woowacourse.shopping.model.OrderHistoriesUIModel
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.OrderHistoryUIModel

fun List<OrderHistory>.toUIModel(): List<OrderHistoryUIModel> = map { it.toUIModel() }

fun OrderHistories.toUIModel(): OrderHistoriesUIModel =
    OrderHistoriesUIModel(
        orderHistories = orderHistories.map { it.toUIModel() },
        lastOrderId = lastOrderId
    )

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
