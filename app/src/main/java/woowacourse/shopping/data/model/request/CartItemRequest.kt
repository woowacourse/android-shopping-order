package woowacourse.shopping.data.model.request

import kotlinx.serialization.SerialName

data class CartItemRequest(
    @SerialName("productId") val productId: Long,
    @SerialName("quantity") val quantity: Int,
)
