package woowacourse.shopping.repository.http.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItemQuantityUpdateRequestDto(
    val quantity: Int,
)
