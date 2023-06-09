package woowacourse.shopping.mapper

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderUIModel

fun Order.toUIModel(): OrderUIModel {
    return OrderUIModel(
        cartProducts = cartItems.map { it.toUIModel() },
        totalPrice = totalPrice,
        currentPoints = currentPoints,
        earnedPoints = earnedPoints,
        availablePoints = availablePoints
    )
}
