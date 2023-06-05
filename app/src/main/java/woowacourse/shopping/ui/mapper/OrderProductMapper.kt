package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.OrderProduct
import woowacourse.shopping.domain.Price
import woowacourse.shopping.ui.model.OrderProductUiModel

fun OrderProductUiModel.toDomainModel() = OrderProduct(
    id = id,
    name = name,
    count = Count(count),
    price = Price(price),
    imageUrl = imageUrl
)

fun OrderProduct.toUiModel() = OrderProductUiModel(
    id = id,
    name = name,
    count = count.value,
    price = price.value,
    imageUrl = imageUrl
)
