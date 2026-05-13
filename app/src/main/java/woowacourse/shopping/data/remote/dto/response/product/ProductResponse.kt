package woowacourse.shopping.data.remote.dto.response.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
