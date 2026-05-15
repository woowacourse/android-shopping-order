package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    @SerialName("content")
    val content: List<Content>,
)
