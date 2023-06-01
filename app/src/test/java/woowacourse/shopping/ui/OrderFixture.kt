package woowacourse.shopping.ui

import woowacourse.shopping.ui.model.Order

object OrderFixture {

    fun createOrders() = listOf(
        Order(
            orderId = 1,
            orderDate = "",
            orderProducts = listOf(),
            totalPrice = 0,
            usedPoint = 0,
            earnedPoint = 0
        ),
        Order(
            orderId = 2,
            orderDate = "",
            orderProducts = listOf(),
            totalPrice = 0,
            usedPoint = 0,
            earnedPoint = 0
        )
    )

    fun createOrder() = Order(
        orderId = 1,
        orderDate = "",
        orderProducts = listOf(),
        totalPrice = 0,
        usedPoint = 0,
        earnedPoint = 0
    )

    fun createBasketsId() = listOf(1, 2, 3, 4, 5)
}
