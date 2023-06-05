package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.model.OrderModel

fun OrderModel.toDomain(): Order = Order(
    id = id,
    orderProducts = orderProducts.toDomain(),
    payment = payment.toDomain(),
)

fun Order.toUi(): OrderModel = OrderModel(
    id = id,
    orderProducts = orderProducts.toUi(),
    payment = payment.toUi(),
)

fun List<Order>.toUi(): List<OrderModel> = map { it.toUi() }
