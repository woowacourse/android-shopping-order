package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponseDto(
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val content: List<Content>,
    val number: Int,
    val sort: Sort,
    val pageable: Pageable,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean,
)
