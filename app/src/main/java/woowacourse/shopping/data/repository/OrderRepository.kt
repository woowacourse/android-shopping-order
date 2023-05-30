package woowacourse.shopping.data.repository

import woowacourse.shopping.ui.model.Order

// todo: 위치가 어디에?
interface OrderRepository {

    fun addOrder(
        basketIds: List<Int>,
        usingPoint: Int,
        totalPrice: Int,
        onAdded: (orderId: Int) -> Unit,
    )

    fun getOrder(
        orderId: Int,
        onReceived: (order: Order) -> Unit,
    )

    fun getOrders(onReceived: (orders: List<Order>) -> Unit)
}
