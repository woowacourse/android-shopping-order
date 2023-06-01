package woowacourse.shopping.ui

import woowacourse.shopping.ui.model.OrderUiModel

object OrderFixture {

    fun createOrders() = listOf(
        OrderUiModel(
            id = 1,
            date = "",
            products = listOf(),
            totalPrice = 0,
            usedPoint = 0,
            earnedPoint = 0
        ),
        OrderUiModel(
            id = 2,
            date = "",
            products = listOf(),
            totalPrice = 0,
            usedPoint = 0,
            earnedPoint = 0
        )
    )

    fun createOrder() = OrderUiModel(
        id = 1,
        date = "",
        products = listOf(),
        totalPrice = 0,
        usedPoint = 0,
        earnedPoint = 0
    )

    fun createBasketsId() = listOf(1, 2, 3, 4, 5)
}
