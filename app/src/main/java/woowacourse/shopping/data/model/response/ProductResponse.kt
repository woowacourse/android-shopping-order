package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName

data class ProductResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("price") val price: Int,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("category") val category: String,
)
