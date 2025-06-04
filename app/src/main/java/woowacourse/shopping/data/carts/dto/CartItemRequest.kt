package woowacourse.shopping.data.carts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    @SerialName("productId")
    val productId: Int,
    @SerialName("quantity")
    val quantity: Int,
)
