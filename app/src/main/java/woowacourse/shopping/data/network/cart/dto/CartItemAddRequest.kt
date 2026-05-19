package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemAddRequest(
    @SerialName("productId")
    val productId: Long,
    @SerialName("quantity")
    val quantity: Int,
)
