package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    val offset: Long,
    val sort: Sort,
    val paged: Boolean,
    val pageNumber: Int,
    val pageSize: Int,
    val unpaged: Boolean,
)
