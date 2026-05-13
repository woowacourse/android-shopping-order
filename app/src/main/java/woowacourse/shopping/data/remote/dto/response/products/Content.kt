package woowacourse.shopping.data.remote.dto.response.products

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
