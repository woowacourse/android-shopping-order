package woowacourse.shopping.repository.http.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequestDto(
    val productId: Long,
    val quantity: Int = 1,
)

@Serializable
data class CartItemQuantityUpdateRequestDto(
    val quantity: Int,
)
