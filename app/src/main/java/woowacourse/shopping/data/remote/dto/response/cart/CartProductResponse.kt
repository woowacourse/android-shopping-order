package woowacourse.shopping.data.remote.dto.response.cart

import kotlinx.serialization.Serializable

@Serializable
data class CartProductResponse(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Long,
)
