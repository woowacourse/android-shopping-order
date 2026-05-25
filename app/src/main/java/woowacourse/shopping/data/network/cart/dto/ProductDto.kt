package woowacourse.shopping.data.network.cart.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
