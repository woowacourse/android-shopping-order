package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
