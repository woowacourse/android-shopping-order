package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequestDto(
    @SerialName("productId")
    val productId: Long,
    @SerialName("quantity")
    val quantity: Int,
)
