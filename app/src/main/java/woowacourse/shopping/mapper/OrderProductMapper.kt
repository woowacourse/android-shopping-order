package woowacourse.shopping.mapper

import woowacourse.shopping.model.OrderProduct
import woowacourse.shopping.uimodel.OrderProductUIModel

fun OrderProduct.toUIModel() = OrderProductUIModel(
    productId = productId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity,
)
