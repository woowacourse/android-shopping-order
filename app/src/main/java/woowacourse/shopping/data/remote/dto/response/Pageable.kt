package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable


@Serializable
data class Pageable(
    val sort: Sort,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)
