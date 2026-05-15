package woowacourse.shopping.data.remote.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val id: Long,
    val product: Product,
    val quantity: Int,
)
