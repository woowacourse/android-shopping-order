package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    @SerialName("content") val content: List<CartItemContent>,
    @SerialName("first") val first: Boolean,
    @SerialName("last") val last: Boolean,
)
