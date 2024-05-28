package woowacourse.shopping.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestProductsPostDto(
    val category: String,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
