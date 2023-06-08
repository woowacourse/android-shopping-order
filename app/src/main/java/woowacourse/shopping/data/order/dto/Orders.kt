package woowacourse.shopping.data.order.dto

import kotlinx.serialization.Serializable

@Serializable
data class Orders(
    val orders: List<Order>,
)
