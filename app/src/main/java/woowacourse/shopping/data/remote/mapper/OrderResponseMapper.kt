package woowacourse.shopping.data.remote.mapper

import woowacourse.shopping.data.model.DataCount
import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.data.model.DataOrderItem
import woowacourse.shopping.data.model.DataPoint
import woowacourse.shopping.data.model.DataPrice
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.data.remote.response.order.Individualorder.IndividualOrderResponse
import woowacourse.shopping.data.remote.response.order.Individualorder.OrderItem
import woowacourse.shopping.util.LocalDateTimeFormatter
import java.time.LocalDateTime

fun OrderItem.toData(): DataOrderItem =
    DataOrderItem(
        count = DataCount(quantity),
        product = DataProduct(
            id = productId,
            name = productName,
            price = DataPrice(price),
            imageUrl = imageUrl
        )
    )

fun IndividualOrderResponse.toData(): DataOrder =
    DataOrder(
        orderId = orderId,
        createdAt = LocalDateTime.parse(createdAt, LocalDateTimeFormatter.hyphenColonFormatter),
        orderItems = orderItems.map { it.toData() },
        totalPrice = DataPrice(totalPrice),
        usedPoint = DataPoint(usedPoint),
        earnedPoint = DataPoint(earnedPoint)
    )
