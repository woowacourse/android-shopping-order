package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String
)