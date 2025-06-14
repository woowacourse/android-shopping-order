package woowacourse.shopping.data.model.response.cartitem

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.response.product.ProductContent

@Serializable
data class CartItemContent(
    @SerialName("id") val id: Long,
    @SerialName("product") val product: ProductContent,
    @SerialName("quantity") val quantity: Int,
)
