package woowacourse.shopping.ui

import woowacourse.shopping.ui.model.OrderUiModel

object OrderFixture {

    fun createOrders() = listOf(
        OrderUiModel(
            orderId = 1,
            orderDate = "",
            uiOrderProducts = listOf(),
            totalPrice = 0,
            usedPoint = 0,
            earnedPoint = 0
        ),
        OrderUiModel(
            orderId = 2,
            orderDate = "",
            uiOrderProducts = listOf(),
            totalPrice = 0,
            usedPoint = 0,
            earnedPoint = 0
        )
    )

    fun createOrder() = OrderUiModel(
        orderId = 1,
        orderDate = "",
        uiOrderProducts = listOf(),
        totalPrice = 0,
        usedPoint = 0,
        earnedPoint = 0
    )

    fun createBasketsId() = listOf(1, 2, 3, 4, 5)
}
