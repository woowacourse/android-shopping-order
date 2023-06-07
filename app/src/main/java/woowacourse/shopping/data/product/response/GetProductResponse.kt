package woowacourse.shopping.data.product.response

import kotlinx.serialization.Serializable

@Serializable
data class GetProductResponse(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String
)