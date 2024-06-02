package woowacourse.shopping.ui.order

import java.io.Serializable

data class OrderInformation(
    val cartItemIds: List<Long>,
    val orderAmount: Int,
    val ordersCount: Int,
) : Serializable
