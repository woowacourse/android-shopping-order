package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.SortDto
import woowacourse.shopping.remote.model.response.SortResponse

fun SortResponse.toData(): SortDto {
    return SortDto(
        sorted = this.sorted,
        unSorted = this.unSorted,
        empty = this.empty,
    )
}
