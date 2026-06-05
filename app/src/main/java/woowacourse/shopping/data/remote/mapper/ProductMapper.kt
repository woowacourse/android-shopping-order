package woowacourse.shopping.data.remote.mapper

import woowacourse.shopping.data.remote.dto.response.product.ProductResponse
import woowacourse.shopping.data.remote.dto.response.products.ProductDto
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

fun ProductDto.toDomain(): Product =
    Product(
        id = id.toString(),
        name = ProductName(name),
        price = Money(price.toLong()),
        imageUrl = imageUrl,
        category = category,
    )

fun ProductResponse.toDomain(): Product =
    Product(
        id = id.toString(),
        name = ProductName(name),
        price = Money(price.toLong()),
        imageUrl = imageUrl,
        category = category,
    )
