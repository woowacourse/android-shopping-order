package woowacourse.shopping.mapper

import woowacourse.shopping.data.dto.OrderInfoDto
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.uimodel.OrderInfoUIModel

fun OrderInfo.toUIModel() = OrderInfoUIModel(
    cartItems = CartProducts(cartItems).toUIModel(),
    totalPrice = totalPrice,
    currentPoints = currentPoints,
    earnedPoints = earnedPoints,
    availablePoints = availablePoints,
)

fun OrderInfoDto.toDomain() = OrderInfo(
    cartItems = cartItems.map { it.toDomain() },
    totalPrice = totalPrice,
    currentPoints = currentPoints,
    earnedPoints = earnedPoints,
    availablePoints = availablePoints,
)
