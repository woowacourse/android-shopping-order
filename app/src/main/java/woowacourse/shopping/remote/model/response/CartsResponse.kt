package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CartsResponse(
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortResponse,
    val first: Boolean,
    val last: Boolean,
    val pageable: PageableResponse,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<CartResponse>,
    val empty: Boolean,
)
