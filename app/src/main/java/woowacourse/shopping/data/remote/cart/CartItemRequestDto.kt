package woowacourse.shopping.data.remote.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequestDto(
    val productId: Long,
    val quantity: Int = 1,
)
