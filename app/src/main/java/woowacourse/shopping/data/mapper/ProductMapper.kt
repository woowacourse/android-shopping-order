package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.datasource.response.ProductEntity
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product

fun ProductEntity.toDomainModel() = Product(
    id = id,
    name = name,
    price = Price(price),
    imageUrl = imageUrl
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    price = price.value,
    imageUrl = imageUrl
)
