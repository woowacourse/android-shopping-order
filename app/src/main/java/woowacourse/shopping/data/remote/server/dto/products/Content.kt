package woowacourse.shopping.data.remote.server.dto.products


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.product.Product

@Serializable
data class Content(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int
)
fun Content.toDomainProduct() =
    Product(
        category = category,
        id = id,
        imageUri = imageUrl.ifBlank { DEFAULT_PRODUCT_IMAGE_URL },
        name = name,
        price = price
    )

private const val DEFAULT_PRODUCT_IMAGE_URL = "https://via.placeholder.com/300"