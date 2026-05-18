package woowacourse.shopping.repository.http.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartPageResponseDto(
    val content: List<CartItemResponseDto>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
)
