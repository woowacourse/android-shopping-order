package woowacourse.shopping.data.model.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quantity(
    @SerialName("quantity")
    val quantity: Int,
)
