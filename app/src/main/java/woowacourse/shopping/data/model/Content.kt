package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int,
)
