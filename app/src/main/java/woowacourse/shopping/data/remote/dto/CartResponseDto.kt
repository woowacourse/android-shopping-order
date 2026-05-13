package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartResponseDto(
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val content: List<CartItemDto>,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean,
)
