package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun DataProduct.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price.toDomain(),
        imageUrl = imageUrl,
    )

fun Product.toData(): DataProduct =
    DataProduct(
        id = id,
        name = name,
        price = price.toData(),
        imageUrl = imageUrl,
    )

fun ProductDto.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = Price(price),
        imageUrl = imageUrl,
    )

fun Product.toDto(): ProductDto =
    ProductDto(
        id = id,
        name = name,
        price = price.value,
        imageUrl = imageUrl,
    )
