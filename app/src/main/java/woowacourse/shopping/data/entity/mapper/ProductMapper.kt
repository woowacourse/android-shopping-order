package woowacourse.shopping.data.entity.mapper

import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun ProductEntity.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = Price(price),
        imageUrl = imageUrl,
    )

fun Product.toEntity(): ProductEntity =
    ProductEntity(
        id = id,
        name = name,
        price = price.value,
        imageUrl = imageUrl,
    )
