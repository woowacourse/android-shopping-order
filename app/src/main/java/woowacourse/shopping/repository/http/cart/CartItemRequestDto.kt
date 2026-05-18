package woowacourse.shopping.repository.http.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequestDto(
    val productId: Long,
    val quantity: Int = 1,
)
