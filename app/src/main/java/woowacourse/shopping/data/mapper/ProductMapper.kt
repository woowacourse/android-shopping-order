package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.entity.ProductEntity
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail

fun ProductEntity.toDomain(): ProductDetail =
    ProductDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
        category = "",
    )

fun ProductsResponse.Content.toDomain(): Product =
    Product(
        productDetail =
            ProductDetail(
                id = id,
                name = name,
                imageUrl = imageUrl,
                price = price,
                category = category,
            ),
        quantity = 0,
    )

fun ProductDetailResponse.toDomain(): ProductDetail =
    ProductDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
        category = category,
    )
