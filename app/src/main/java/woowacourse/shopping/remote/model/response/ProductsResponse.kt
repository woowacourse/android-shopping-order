package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    val content: List<ProductResponse>,
    val pageable: PageableResponse,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortResponse,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
)
