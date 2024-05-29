package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.CartsDto
import woowacourse.shopping.remote.model.response.CartsResponse

fun CartsResponse.toData(): CartsDto {
    return CartsDto(
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        sortDto = this.sortResponse.toData(),
        first = this.first,
        last = this.last,
        pageableDto = this.pageableResponse.toData(),
        number = this.number,
        numberOfElements = this.numberOfElements,
        size = this.size,
        content = this.content.map { it.toData() },
        empty = this.empty,
    )
}
