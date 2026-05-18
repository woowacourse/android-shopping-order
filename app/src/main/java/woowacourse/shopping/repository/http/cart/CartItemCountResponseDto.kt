package woowacourse.shopping.repository.http.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItemCountResponseDto(
    val quantity: Int,
)
