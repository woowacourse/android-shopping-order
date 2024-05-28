package woowacourse.shopping.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RequestProductsGetDto(
    val totalPages: Int,
    val totalElements: Int,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val pageable: Pageable,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<Content>,
    val empty: Boolean
)
