package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Price
import woowacourse.shopping.ui.model.OrderUiModel

fun Order.toUiModel() = OrderUiModel(
    id = id,
    date = date,
    products = products.map { it.toUiModel() },
    totalPrice = totalPrice.value,
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)

fun OrderUiModel.toDomainModel() = Order(
    id = id,
    date = date,
    products = products.map { it.toDomainModel() },
    totalPrice = Price(totalPrice),
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)
