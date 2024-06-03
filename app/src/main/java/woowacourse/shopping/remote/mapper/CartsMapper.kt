package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.remote.model.response.CartsResponse

fun CartsResponse.toDomain(): Carts {
    return Carts(
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        sort = this.sortResponse.toDomain(),
        first = this.first,
        last = this.last,
        pageable = this.pageableResponse.toDomain(),
        number = this.number,
        numberOfElements = this.numberOfElements,
        size = this.size,
        content = this.content.map { it.toDomain() },
        empty = this.empty,
    )
}
