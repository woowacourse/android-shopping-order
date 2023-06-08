package woowacourse.shopping.data.model.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String
)
