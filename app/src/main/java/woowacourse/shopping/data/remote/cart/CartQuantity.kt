package woowacourse.shopping.data.remote.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartQuantity(
    @SerialName("quantity")
    val quantity: Int,
)
