package woowacourse.shopping.backend.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingCartResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: ShoppingCartPageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Long,
    val totalPages: Int,
)
