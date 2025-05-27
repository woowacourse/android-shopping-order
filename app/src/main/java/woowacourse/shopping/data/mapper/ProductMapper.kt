package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.entity.ProductEntity
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse
import woowacourse.shopping.domain.model.CatalogProduct
import woowacourse.shopping.domain.model.ProductDetail

fun ProductEntity.toDomain(): ProductDetail =
    ProductDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
    )

fun ProductsResponse.Content.toDomain(): CatalogProduct =
    CatalogProduct(
        productDetail =
            ProductDetail(
                id = id.toInt(),
                name = name,
                imageUrl = imageUrl,
                price = price,
            ),
        quantity = 0,
    )

fun ProductDetailResponse.toDomain(): ProductDetail =
    ProductDetail(
        id = id.toInt(),
        name = name,
        imageUrl = imageUrl,
        price = price,
    )
