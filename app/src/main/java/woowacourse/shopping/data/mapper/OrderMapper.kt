package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.OrderResponse
import woowacourse.shopping.ui.model.OrderUiModel

// todo 지금은 분리의 의미가 없긴하다.
fun OrderResponse.toOrder() = OrderUiModel(
    orderId = orderId,
    orderDate = orderedTime,
    uiOrderProducts = orderDetails.map { it.toOrderDetail() },
    totalPrice = totalPrice,
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)
