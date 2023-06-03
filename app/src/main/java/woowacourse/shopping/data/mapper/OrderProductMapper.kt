package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.OrderProductEntity
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.OrderProduct
import woowacourse.shopping.domain.Price

fun OrderProductEntity.toOrderProductDomainModel() = OrderProduct(
    id = productId.toInt(),
    name = productName,
    count = Count(quantity.toInt()),
    price = Price(price.toInt()),
    imageUrl = imageUrl,
)
