package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderList
import woowacourse.shopping.model.OrderListUIModel

fun OrderList.toUIModel(): OrderListUIModel {
    return OrderListUIModel(
        cartProducts = cartItems.map { it.toUIModel(true) },
        totalPrice = totalPrice,
        currentPoints = currentPoints,
        earnedPoints = earnedPoints,
        availablePoints = availablePoints
    )
}
