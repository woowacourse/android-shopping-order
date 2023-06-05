package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
