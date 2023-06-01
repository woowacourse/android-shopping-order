package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.OrderResponse
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Price

// todo 지금은 분리의 의미가 없긴하다.
fun OrderResponse.toOrderDomainModel() = Order(
    id = orderId.toInt(),
    date = orderedTime,
    products = orderProducts.map { it.toOrderProductDomainModel() },
    totalPrice = Price(totalPrice.toInt()),
    usedPoint = usedPoint.toInt(),
    earnedPoint = earnedPoint.toInt()
)
