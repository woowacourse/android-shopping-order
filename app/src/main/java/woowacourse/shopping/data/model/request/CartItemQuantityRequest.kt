package woowacourse.shopping.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemQuantityRequest(
    @SerialName("quantity")
    val quantity: Int,
)
