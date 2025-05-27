package woowacourse.shopping.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartProductRequestDto(
    @SerialName("productId")
    val productId: Int,
    @SerialName("quantity")
    val quantity: Int,
)
