package woowacourse.shopping.data.remote.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartPageResponseDto(
    val content: List<CartItemResponseDto>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
)
