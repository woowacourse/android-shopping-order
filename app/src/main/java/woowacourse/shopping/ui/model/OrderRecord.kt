package woowacourse.shopping.ui.model

import java.io.Serializable
import java.util.Date

data class OrderRecord(
    val orderId: Long,
    val orderedTime: Date?,
    val orderProducts: List<OrderProduct>,
    val totalPrice: Long,
    val usedPoint: Long,
    val earnedPoint: Long,
) : Serializable
