package woowacourse.shopping.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    @SerialName("productId")
    val productId: Long,
    @SerialName("quantity")
    val quantity: Int,
)
