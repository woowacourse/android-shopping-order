package woowacourse.shopping.data.source.remote.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartContent(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val product: Product,
    @SerialName("quantity")
    val quantity: Int,
)
