package woowacourse.shopping.presentation.mapper

import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.model.OrderModel
import woowacouse.shopping.model.order.Order
import woowacouse.shopping.model.order.OrderDetail

fun Order.toUIModel(): OrderModel =
    OrderModel(id, cartProducts.toUIModel(), usePoint.toUIModel(), card.toUIModel())

fun OrderModel.toModel(): Order =
    Order(id, cartProducts.toModel(), usePoint.toModel(), card.toModel())

fun OrderDetail.toUIModel(): OrderDetailModel = OrderDetailModel(
    id,
    usedPoint,
    savedPoint,
    orderDateTime,
    products.map { it.toUIModel() }
)

fun OrderDetailModel.toModel(): OrderDetail = OrderDetail(
    id,
    usedPoint,
    savedPoint,
    orderDateTime,
    products.map { it.toModel() }
)
