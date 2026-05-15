package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuantityDto(
    @SerialName("quantity")
    val quantity: Int,
)
