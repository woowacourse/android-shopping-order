package woowacourse.shopping.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemCountResponse(
    @SerialName("quantity")
    val quantity: Int,
)
