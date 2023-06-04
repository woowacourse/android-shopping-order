package woowacourse.shopping.data.order.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderCartItemDto(
    val cartItemId: Long,
    val orderCartItemName: String,
    val orderCartItemPrice: Int,
    val orderCartItemImageUrl: String,
)
