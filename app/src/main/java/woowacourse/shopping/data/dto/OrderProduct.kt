package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderProduct(
    val name: String,
    val imageUrl: String,
    val quantity: Int,
    val price: Int
)