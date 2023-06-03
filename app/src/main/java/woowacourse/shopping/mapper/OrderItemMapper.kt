package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderHistoryItem
import woowacourse.shopping.model.OrderHistoryItemUIModel

fun OrderHistoryItem.toUIModel(): OrderHistoryItemUIModel = OrderHistoryItemUIModel(
    productId = productId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity
)
