package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
