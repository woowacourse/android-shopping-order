package woowacourse.shopping.data.carts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartQuantity(
    @SerialName("quantity")
    val quantity: Int,
)
