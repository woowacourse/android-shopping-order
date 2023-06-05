package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.OrderEntity
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Price

fun OrderEntity.toOrderDomainModel() = Order(
    id = orderId.toInt(),
    date = orderedTime,
    products = orderProducts.map { it.toOrderProductDomainModel() },
    totalPrice = Price(totalPrice.toInt()),
    usedPoint = usedPoint.toInt(),
    earnedPoint = earnedPoint.toInt()
)
