package woowacourse.shopping.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
