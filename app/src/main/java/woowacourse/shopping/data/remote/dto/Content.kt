package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
