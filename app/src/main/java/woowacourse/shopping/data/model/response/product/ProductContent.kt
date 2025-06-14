package woowacourse.shopping.data.model.response.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductContent(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("price") val price: Long,
    @SerialName("category") val category: String,
)
