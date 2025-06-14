package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemQuantityRequestDto(
    @SerialName("quantity")
    val quantity: Int,
)
