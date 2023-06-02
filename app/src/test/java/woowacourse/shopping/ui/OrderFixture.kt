package woowacourse.shopping.ui

import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.Price

object OrderFixture {

    fun createOrders() = listOf(
        Order(
            id = 1,
            date = "",
            products = listOf(),
            totalPrice = Price(0),
            usedPoint = 0,
            earnedPoint = 0
        ),
        Order(
            id = 2,
            date = "",
            products = listOf(),
            totalPrice = Price(0),
            usedPoint = 0,
            earnedPoint = 0
        )
    )

    fun createOrder() = Order(
        id = 1,
        date = "",
        products = listOf(),
        totalPrice = Price(0),
        usedPoint = 0,
        earnedPoint = 0
    )

    fun createBasketsId() = listOf(1, 2, 3, 4, 5)
}
