package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingCartItemsResponseDto(
    @SerialName("content")
    val shoppingCartItems: List<ShoppingCartItemResponseDto>,
    @SerialName("last")
    val last: Boolean,
)
