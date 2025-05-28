package woowacourse.shopping.data.model.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

@Serializable
data class ProductResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("category")
    val category: String?,
)

fun ProductResponse.toDomain() =
    Product(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = Price(price),
        category = category,
    )
