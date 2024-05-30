package woowacourse.shopping.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartCountResponse(
    @SerialName("quantity")
    val quantity: Int,
)
