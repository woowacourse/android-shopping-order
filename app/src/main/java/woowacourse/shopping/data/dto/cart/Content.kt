package woowacourse.shopping.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

@Serializable
data class Content(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val product: CartResponse,
    @SerialName("quantity")
    val quantity: Int,
)

fun Content.toDomain(): Product =
    Product(
        productId = this.id,
        name = this.name,
        _price = Price(this.price),
        imageUrl = this.imageUrl,
        category = this.category,
    )
