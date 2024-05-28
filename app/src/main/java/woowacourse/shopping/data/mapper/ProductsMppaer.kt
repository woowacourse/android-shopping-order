package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.ProductsDto
import woowacourse.shopping.domain.model.Products

fun ProductsDto.toDomain(): Products {
    return Products(
        content = this.content.map { it.toDomain() },
        pageable = this.pageableDto.toDomain(),
        last = this.last,
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        sort = this.sortDto.toDomain(),
        first = this.first,
        number = this.number,
        numberOfElements = this.numberOfElements,
        size = this.size,
        empty = this.empty,
    )
}
