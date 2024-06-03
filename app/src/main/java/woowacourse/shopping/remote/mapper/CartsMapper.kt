package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.remote.model.response.CartsResponse

fun CartsResponse.toDomain(): Carts {
    return Carts(
        totalElements = this.totalElements,
        last = this.last,
        pageable = this.pageableResponse.toDomain(),
        content = this.content.map { it.toDomain() },
    )
}
