package woowacourse.shopping.data.remote.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
