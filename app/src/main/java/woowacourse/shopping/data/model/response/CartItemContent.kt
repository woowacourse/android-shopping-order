package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemContent(
    @SerialName("id") val id: Long,
    @SerialName("product") val product: ProductContent,
    @SerialName("quantity") val quantity: Int,
)
