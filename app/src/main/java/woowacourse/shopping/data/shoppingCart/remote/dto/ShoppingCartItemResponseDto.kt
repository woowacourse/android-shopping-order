package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingCartItemResponseDto(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val product: ProductResponseDto,
    @SerialName("quantity")
    val quantity: Int,
)
