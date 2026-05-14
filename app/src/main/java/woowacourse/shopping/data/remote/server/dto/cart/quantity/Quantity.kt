package woowacourse.shopping.data.remote.server.dto.cart.quantity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quantity(
    @SerialName("quantity")
    val quantity: Int
)