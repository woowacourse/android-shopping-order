package woowacourse.shopping.ui.model

import java.io.Serializable

data class Order(
    val orderId: Long,
    val orderDate: String?,
    val orderProducts: List<OrderProduct>,
    val totalPrice: Long,
    val usedPoint: Long,
    val earnedPoint: Long,
) : Serializable
