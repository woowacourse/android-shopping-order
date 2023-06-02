package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.OrderDetailEntity
import woowacouse.shopping.model.order.OrderDetail

fun OrderDetailEntity.toModel(): OrderDetail = OrderDetail(
    id,
    usedPoint,
    savedPoint,
    orderedAt,
    products.map { it.toModel() }
)
