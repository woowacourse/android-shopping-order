package woowacourse.shopping.data.network.product.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductPageResponse(
    val content: List<ProductDto>,
)
