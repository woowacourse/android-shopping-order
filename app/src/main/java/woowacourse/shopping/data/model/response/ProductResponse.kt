package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("price") val price: Int,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("category") val category: String,
)
