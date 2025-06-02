package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartCountsResponse(
    @SerialName("quantity")
    val quantity: Int,
)
