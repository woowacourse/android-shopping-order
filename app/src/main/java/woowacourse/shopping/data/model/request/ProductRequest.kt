package woowacourse.shopping.data.model.request

import kotlinx.serialization.SerialName

data class ProductRequest(
    @SerialName("name") val name: String,
    @SerialName("price") val price: Long,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("category") val category: String,
)
