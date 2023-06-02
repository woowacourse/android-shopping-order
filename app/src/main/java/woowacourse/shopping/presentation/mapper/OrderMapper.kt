package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.OrderModel
import woowacouse.shopping.model.order.Order

fun Order.toUIModel(): OrderModel = OrderModel(id, cartProducts.toUIModel(), usePoint.toUIModel(), card.toUIModel())

fun OrderModel.toModel(): Order = Order(id, cartProducts.toModel(), usePoint.toModel(), card.toModel())
