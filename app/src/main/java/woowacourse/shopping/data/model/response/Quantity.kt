package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quantity(
    @SerialName("quantity") val quantity: Int,
)
