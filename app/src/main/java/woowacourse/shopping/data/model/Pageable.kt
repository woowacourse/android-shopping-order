package woowacourse.shopping.data.model

import kotlin.Int

data class Pageable(
    val sort: Sort,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean,
) {
    companion object {
        val EMPTY =
            Pageable(
                sort = Sort.EMPTY,
                pageNumber = 0,
                pageSize = 0,
                offset = 0,
                paged = false,
                unpaged = false,
            )
    }
}
