package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.Content
import woowacourse.shopping.data.remote.dto.ProductResponseDto
import woowacourse.shopping.data.remote.dto.ProductsResponseDto
import woowacourse.shopping.domain.product.Category
import woowacourse.shopping.domain.product.ImageUrl
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductName
import woowacourse.shopping.domain.product.Products

fun ProductsResponseDto.toDomain(): Products =
    Products(
        items = this.content.map { it.toDomain() },
        isLast = this.last,
    )

fun Content.toDomain(): Product =
    Product(
        id = this.id,
        imageUrl = ImageUrl(this.imageUrl),
        name = ProductName(this.name),
        price = Price(this.price),
        category = Category(this.category),
    )

fun ProductResponseDto.toDomain(): Product =
    Product(
        id = this.id,
        name = ProductName(this.name),
        price = Price(this.price),
        imageUrl = ImageUrl(this.imageUrl),
        category = Category(this.category),
    )
