package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderProductDto(
    val productId: Int,
    val quantity: Int
)
