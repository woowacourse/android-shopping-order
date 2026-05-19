package woowacourse.shopping.data.network.product.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("content")
    val content: List<ProductItemDto>,
    @SerialName("last")
    val last: Boolean,
)
