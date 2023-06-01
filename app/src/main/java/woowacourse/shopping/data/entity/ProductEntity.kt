package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class ProductEntity(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String
)