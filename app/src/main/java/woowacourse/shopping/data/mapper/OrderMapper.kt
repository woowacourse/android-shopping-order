package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.ui.model.Order

// todo 지금은 분리의 의미가 없긴하다.
fun DataOrder.toOrder() = Order(
    orderId = orderId,
    orderedTime = orderedTime,
    orderProducts = orderDetails.map { it.toOrderDetail() },
    totalPrice = totalPrice,
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)
