package woowacourse.shopping.repository.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponseDto(
    val id: Long,
    val quantity: Int,
    val product: ProductResponseDto,
)

@Serializable
data class CartPageResponseDto(
    val content: List<CartItemResponseDto>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
)

@Serializable
data class CartItemCountResponseDto(
    val quantity: Int,
)
