package woowacourse.shopping.repository.http.dto.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItemCountResponseDto(
    val quantity: Int,
)
