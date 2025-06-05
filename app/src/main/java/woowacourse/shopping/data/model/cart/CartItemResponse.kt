package woowacourse.shopping.data.model.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.product.ProductResponse

@Serializable
data class CartItemResponse(
    @SerialName("id")
    val cartId: Long,
    @SerialName("product")
    val product: ProductResponse,
    @SerialName("quantity")
    val quantity: Int,
)
