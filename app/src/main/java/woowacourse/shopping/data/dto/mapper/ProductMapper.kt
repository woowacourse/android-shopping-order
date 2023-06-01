package woowacourse.shopping.data.dto.mapper

import woowacourse.shopping.data.dto.ProductDeleteRequest
import woowacourse.shopping.data.dto.ProductGetResponse
import woowacourse.shopping.data.dto.ProductPostRequest
import woowacourse.shopping.data.dto.ProductPutRequest
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun ProductGetResponse.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = Price(price),
        imageUrl = imageUrl,
    )

fun Product.toProductPostRequest(): ProductPostRequest =
    ProductPostRequest(
        name = name,
        price = price.value,
        imageUrl = imageUrl,
    )

fun Product.toProductPutRequest(): ProductPutRequest =
    ProductPutRequest(
        name = name,
        price = price.value,
        imageUrl = imageUrl,
    )

fun Product.toProductDeleteRequest(): ProductDeleteRequest =
    ProductDeleteRequest(
        name = name,
        price = price.value,
        imageUrl = imageUrl,
    )

