package woowacourse.shopping.data.model.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderProductDto(
    val productId: Int,
    val quantity: Int
)
