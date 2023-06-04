package woowacourse.shopping.data.order.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderCartItemDtos(
    val orderCartItemDtos: List<OrderCartItemDto>,
)
