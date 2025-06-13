package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemCount(
    val quantity: Int,
)
