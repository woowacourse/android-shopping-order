package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.PageableDto
import woowacourse.shopping.remote.model.response.PageableResponse

fun PageableResponse.toData(): PageableDto {
    return PageableDto(
        sort = this.sort.toData(),
        pageNumber = this.pageNumber,
        pageSize = this.pageSize,
        offset = this.offset,
        paged = this.paged,
        unPaged = this.unPaged,
    )
}
