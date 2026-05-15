package woowacourse.shopping.data.remote.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
