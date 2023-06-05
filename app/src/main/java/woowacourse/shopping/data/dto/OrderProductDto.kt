package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderProductDto(
    val productId: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int,
)
