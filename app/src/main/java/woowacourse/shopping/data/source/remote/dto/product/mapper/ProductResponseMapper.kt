package woowacourse.shopping.data.source.remote.dto.product.mapper

import woowacourse.shopping.data.source.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName

fun ProductResponse.toDomain(): Product =
    Product(
        id = id,
        name = ProductName(name),
        price = Money(price.toLong()),
        imageUrl = imageUrl,
        category = category,
    )
