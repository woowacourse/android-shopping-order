package woowacourse.shopping.data.network.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductPageResponse(
    val content: List<ProductDto>,
)
