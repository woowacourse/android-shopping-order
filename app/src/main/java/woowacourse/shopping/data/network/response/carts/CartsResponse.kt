package woowacourse.shopping.data.network.response.carts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartsResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortX,
    val totalElements: Int,
    val totalPages: Int
)
