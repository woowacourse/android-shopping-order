package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartCountsDto(
    @SerialName("quantity")
    val quantity: Int,
)
