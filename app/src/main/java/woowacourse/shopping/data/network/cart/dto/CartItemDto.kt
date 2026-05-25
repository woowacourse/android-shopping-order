package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)
