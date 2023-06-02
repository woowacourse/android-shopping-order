package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderItem
import woowacourse.shopping.model.OrderItemUIModel

fun OrderItem.toUIModel(): OrderItemUIModel = OrderItemUIModel(
    productId = productId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity
)
