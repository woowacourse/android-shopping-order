package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.ProductsDto
import woowacourse.shopping.remote.model.response.ProductsResponse

fun ProductsResponse.toData(): ProductsDto {
    return ProductsDto(
        content = this.content.map { it.toData() },
        pageableDto = this.pageableResponse.toData(),
        last = this.last,
        totalPages = this.totalPages,
        totalElements = this.totalElements,
        sortDto = this.sortResponse.toData(),
        first = this.first,
        number = this.number,
        numberOfElements = this.numberOfElements,
        size = this.size,
        empty = this.empty,
    )
}
