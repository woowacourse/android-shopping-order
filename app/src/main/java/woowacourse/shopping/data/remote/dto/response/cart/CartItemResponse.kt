package woowacourse.shopping.data.remote.dto.response.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    val id: Long,
    val product: CartProductResponse,
    val quantity: Int,
)
