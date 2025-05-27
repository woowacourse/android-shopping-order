package woowacourse.shopping.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartContent(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val cartProduct: CartResponse,
    @SerialName("quantity")
    val quantity: Int,
)
