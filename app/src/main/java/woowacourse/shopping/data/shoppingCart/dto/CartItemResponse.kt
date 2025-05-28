package woowacourse.shopping.data.shoppingCart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    @SerialName("quantity") val quantity: Int,
)
