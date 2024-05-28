package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.PageableDto
import woowacourse.shopping.domain.model.Pageable

fun PageableDto.toDomain(): Pageable {
    return Pageable(
        sort = this.sortDto.toDomain(),
        pageNumber = this.pageNumber,
        pageSize = this.pageSize,
        offset = this.offset,
        paged = this.paged,
        unPaged = this.unPaged,
    )
}
