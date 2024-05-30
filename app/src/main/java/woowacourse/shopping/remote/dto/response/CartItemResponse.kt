package woowacourse.shopping.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("quantity")
    val count: Int,
    @SerialName("product")
    val product: ProductResponse,
)
