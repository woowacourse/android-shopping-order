package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.SortDto
import woowacourse.shopping.domain.model.Sort

fun SortDto.toDomain(): Sort {
    return Sort(
        sorted = this.sorted,
        unSorted = this.unSorted,
        empty = this.empty,
    )
}
