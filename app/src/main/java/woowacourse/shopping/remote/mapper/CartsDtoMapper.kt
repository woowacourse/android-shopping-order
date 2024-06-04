package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.CartsDto
import woowacourse.shopping.remote.model.response.CartsResponse

fun CartsResponse.toData(): CartsDto {
    return CartsDto(
        content = this.content.map { it.toData() },
        pageable = this.pageable.toData(),
        totalElements = this.totalElements,
        last = this.last,
    )
}
