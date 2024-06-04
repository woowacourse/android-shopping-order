package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.remote.model.response.ProductsResponse

fun ProductsResponse.toDomain(): Products {
    return Products(
        content = this.content.map { it.toDomain() },
        pageable = this.pageableResponse.toDomain(),
    )
}
