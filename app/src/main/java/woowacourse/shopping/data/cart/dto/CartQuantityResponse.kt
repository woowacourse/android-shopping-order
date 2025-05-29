package woowacourse.shopping.data.cart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartQuantityResponse(
    @SerialName("quantity")
    val quantity: Int?,
)
