package woowacourse.shopping.data.product.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    @SerialName("content")
    val products: List<ProductResponse>,
)
