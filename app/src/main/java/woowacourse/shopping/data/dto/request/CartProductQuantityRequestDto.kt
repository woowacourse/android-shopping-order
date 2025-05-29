package woowacourse.shopping.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartProductQuantityRequestDto(
    @SerialName("quantity")
    val quantity: Int,
)
