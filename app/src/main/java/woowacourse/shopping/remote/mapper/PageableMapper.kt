package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Pageable
import woowacourse.shopping.remote.model.response.PageableResponse

fun PageableResponse.toDomain(): Pageable {
    return Pageable(
        sort = this.sortResponse.toDomain(),
        pageNumber = this.pageNumber,
        pageSize = this.pageSize,
        offset = this.offset,
        paged = this.paged,
        unPaged = this.unPaged,
    )
}
