package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.CartsDto
import woowacourse.shopping.domain.model.Carts

fun CartsDto.toDomain(): Carts {
    return Carts(
        content = this.content.map { it.toDomain() },
        pageable = this.pageableDto.toDomain(),
        totalElements = this.totalElements,
        last = this.last,
    )
}
