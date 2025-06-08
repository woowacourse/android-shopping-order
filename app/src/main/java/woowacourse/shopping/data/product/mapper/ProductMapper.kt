package woowacourse.shopping.data.product.mapper

import woowacourse.shopping.data.product.remote.dto.ProductResponseDto
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto
import woowacourse.shopping.domain.product.Product

fun ProductsResponseDto.toDomain(): List<Product> = products.map(ProductResponseDto::toDomain)

fun ProductResponseDto.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )
