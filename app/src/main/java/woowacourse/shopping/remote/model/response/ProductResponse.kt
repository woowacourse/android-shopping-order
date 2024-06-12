package woowacourse.shopping.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val imageUrl: String,
)
