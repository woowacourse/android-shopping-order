package woowacourse.shopping.backend.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
