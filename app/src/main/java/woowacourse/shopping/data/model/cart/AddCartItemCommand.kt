package woowacourse.shopping.data.model.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCartItemCommand(
    @SerialName("productId")
    val productId: Long,
    @SerialName("quantity")
    val quantity: Int,
)
