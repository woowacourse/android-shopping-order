package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartProductDto(
    @SerialName("id")
    val id: Long,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("product")
    val product: ProductDto,
)
