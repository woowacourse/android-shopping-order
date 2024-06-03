package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.remote.model.response.ProductsResponse

fun ProductsResponse.toDomain(): Products {
    return Products(
        content = this.content.map { it.toDomain() },
        pageable = this.pageableResponse.toDomain(),
        last = this.last,
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        sort = this.sortResponse.toDomain(),
        first = this.first,
        number = this.number,
        numberOfElements = this.numberOfElements,
        size = this.size,
        empty = this.empty,
    )
}
