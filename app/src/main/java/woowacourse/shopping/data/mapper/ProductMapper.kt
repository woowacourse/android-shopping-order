package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.ProductResponseDto
import woowacourse.shopping.data.remote.dto.ProductsResponseDto
import woowacourse.shopping.domain.model.product.Category
import woowacourse.shopping.domain.model.product.ImageUrl
import woowacourse.shopping.domain.model.product.Price
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.ProductName
import woowacourse.shopping.domain.model.product.Products

fun ProductsResponseDto.toDomain(): Products =
    Products(
        items = this.content.map { it.toDomain() },
        isLast = this.last,
    )

fun ProductResponseDto.toDomain(): Product =
    Product(
        id = this.id,
        imageUrl = ImageUrl(this.imageUrl),
        name = ProductName(this.name),
        price = Price(this.price),
        category = Category(this.category),
    )
