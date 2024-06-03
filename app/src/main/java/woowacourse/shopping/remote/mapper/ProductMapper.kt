package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.model.response.ProductResponse

fun ProductResponse.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )
}
