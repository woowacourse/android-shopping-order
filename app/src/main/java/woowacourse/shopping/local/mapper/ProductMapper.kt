package woowacourse.shopping.local.mapper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.local.model.ProductHistoryEntity

fun ProductHistoryEntity.toDomain(): Product {
    return Product(
        id = this.productId,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        category = this.category,
    )
}
