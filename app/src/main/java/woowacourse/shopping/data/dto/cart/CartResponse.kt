package woowacourse.shopping.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

@Serializable
data class CartResponse(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
)

fun CartResponse.toDomain(): Product =
    Product(
        this.id,
        this.name,
        Price(this.price),
        this.imageUrl,
        this.category,
    )
