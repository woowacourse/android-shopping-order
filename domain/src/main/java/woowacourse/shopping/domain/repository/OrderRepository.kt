package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Order

interface OrderRepository {

    fun addOrder(
        basketIds: List<Int>,
        usingPoint: Int,
        totalPrice: Int,
        onAdded: (orderId: Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun getOrder(
        orderId: Int,
        onReceived: (order: Order) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )

    fun getOrders(
        onReceived: (orders: List<Order>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    )
}
