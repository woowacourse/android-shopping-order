package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    @SerialName("content") val productContent: List<ProductContent>,
    @SerialName("first") val first: Boolean,
    @SerialName("last") val last: Boolean,
)
