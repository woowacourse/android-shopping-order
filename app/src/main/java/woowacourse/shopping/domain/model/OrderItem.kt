package woowacourse.shopping.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val cartItemId: Long,
    val productId: Long,
    val quantity: Int,
)
