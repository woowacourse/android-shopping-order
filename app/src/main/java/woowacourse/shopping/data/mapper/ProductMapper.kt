package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.dto.ProductDto
import woowacourse.shopping.domain.model.Product

fun ProductDto.toDomainModel(): Product {
    return Product(
        id = this.id.toLong(),
        name = this.name,
        price = this.price.toLong(),
        imageUrl = this.imageUrl,
        category = this.category,
    )
}
