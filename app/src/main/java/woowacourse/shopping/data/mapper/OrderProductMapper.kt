package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.OrderProductEntity
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.OrderProduct
import woowacourse.shopping.domain.Price

// todo 지금은 분리의 의미가 없긴하다.
fun OrderProductEntity.toOrderProductDomainModel() = OrderProduct(
    id = productId.toInt(),
    name = productName,
    count = Count(quantity.toInt()),
    price = Price(price.toInt()),
    imageUrl = imageUrl,
)
