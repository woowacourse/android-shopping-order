package woowacourse.shopping.data.remote.server.dto.cart.items


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.order.PurchaseProduct

@Serializable
data class ContentResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val product: ProductResponse,
    @SerialName("quantity")
    val quantity: Int
)
fun ContentResponse.toDomainPurchaseProduct() = PurchaseProduct(
    id = id,
    product = product.toDomainProduct(),
    count = quantity
)
