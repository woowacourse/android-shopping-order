package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.OrderResponse
import woowacourse.shopping.ui.model.OrderUiModel
// todo data layer에서 왜 ui를?
// todo 지금은 분리의 의미가 없긴하다.
fun OrderResponse.toOrderUiModel() = OrderUiModel(
    orderId = orderId,
    orderDate = orderedTime,
    uiOrderProducts = orderDetails.map { it.toOrderProductUiModel() },
    totalPrice = totalPrice,
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)
