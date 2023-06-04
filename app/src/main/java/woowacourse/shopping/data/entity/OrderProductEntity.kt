package woowacourse.shopping.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class OrderProductEntity(
    val name: String,
    val imageUrl: String,
    val count: Int,
    val price: Int
)