package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Price
import woowacourse.shopping.ui.model.OrderUiModel

fun Order.toOrderUiModel() = OrderUiModel(
    id = id,
    date = date,
    products = products.map { it.toOrderProductUiModel() },
    totalPrice = totalPrice.value,
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)

fun OrderUiModel.toOrderDomainModel() = Order(
    id = id,
    date = date,
    products = products.map { it.toOrderProductDomainModel() },
    totalPrice = Price(totalPrice),
    usedPoint = usedPoint,
    earnedPoint = earnedPoint
)
