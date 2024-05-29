package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.CartsDto
import woowacourse.shopping.domain.model.Carts

fun CartsDto.toDomain(): Carts {
    return Carts(
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        sort = this.sortDto.toDomain(),
        first = this.first,
        last = this.last,
        pageable = this.pageableDto.toDomain(),
        number = this.number,
        numberOfElements = this.numberOfElements,
        size = this.size,
        content = this.content.map { it.toDomain() },
        empty = this.empty,
    )
}
