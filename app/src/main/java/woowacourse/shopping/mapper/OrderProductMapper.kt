package woowacourse.shopping.mapper

import woowacourse.shopping.data.dto.OrderProductDto
import woowacourse.shopping.model.OrderProduct
import woowacourse.shopping.uimodel.OrderProductUIModel

fun OrderProduct.toUIModel() = OrderProductUIModel(
    productId = productId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity,
)

fun OrderProductDto.toDomain() = OrderProduct(
    productId = productId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity,
)
