package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.domain.model.Product

fun ProductResponse.toDomain() =
    Product(
        id = this.id,
        imageUrl = this.imageUrl,
        name = this.name,
        price = this.price,
        quantity = 0,
        category = this.category,
    )
