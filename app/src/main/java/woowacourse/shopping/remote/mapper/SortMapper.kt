package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Sort
import woowacourse.shopping.remote.model.response.SortResponse

fun SortResponse.toDomain(): Sort {
    return Sort(
        sorted = this.sorted,
        unSorted = this.unSorted,
        empty = this.empty,
    )
}
