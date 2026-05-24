package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.retrofit.dto.ProductDto
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductTitle

fun ProductDto.toDomainProduct(): Product =
    Product(
        id = id,
        title = ProductTitle(name),
        price = Price(price),
        imageUrl = imageUrl,
        category = category,
    )

fun ProductResponse.toDomainProducts(): List<Product> = content.map { product -> product.toDomainProduct() }
