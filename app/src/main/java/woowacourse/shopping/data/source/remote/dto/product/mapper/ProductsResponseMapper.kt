package woowacourse.shopping.data.source.remote.dto.product.mapper

import woowacourse.shopping.data.source.remote.dto.product.ProductsResponse
import woowacourse.shopping.domain.model.ProductsPage

fun ProductsResponse.toDomain(): ProductsPage =
    ProductsPage(
        products = content.map { it.toDomain() },
        isLast = last,
    )
