package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartProductQuantityResponseDto(
    @SerialName("quantity")
    val quantity: Int,
)
