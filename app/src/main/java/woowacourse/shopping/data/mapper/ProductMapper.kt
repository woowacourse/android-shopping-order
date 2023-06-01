package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product

fun ProductEntity.toProductDomainModel() = Product(
    id = id,
    name = name,
    price = Price(price),
    imageUrl = imageUrl
)

fun Product.toProductEntity() = ProductEntity(
    id = id,
    name = name,
    price = price.value,
    imageUrl = imageUrl
)
