package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Product

@Serializable
data class ProductDto(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("category")
    val category: String,
)

fun ProductDto.toProduct(): Product =
    Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )
