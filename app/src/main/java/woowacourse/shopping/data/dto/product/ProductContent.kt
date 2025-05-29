package woowacourse.shopping.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

@Serializable
data class ProductContent(
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

fun ProductContent.toDomain(): Product =
    Product(
        productId = this.id,
        name = this.name,
        _price = Price(this.price),
        imageUrl = this.imageUrl,
        category = this.category,
    )
