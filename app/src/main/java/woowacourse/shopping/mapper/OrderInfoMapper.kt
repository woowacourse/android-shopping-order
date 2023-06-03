package woowacourse.shopping.mapper

import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.model.OrderInfoUIModel

fun OrderInfo.toUIModel() = OrderInfoUIModel(
    cartItems = CartProducts(cartItems).toUIModel(),
    totalPrice = totalPrice,
    currentPoints = currentPoints,
    earnedPoints = earnedPoints,
    availablePoints = availablePoints,
)
