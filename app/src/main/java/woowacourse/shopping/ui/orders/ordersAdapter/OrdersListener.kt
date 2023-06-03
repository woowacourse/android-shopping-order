package woowacourse.shopping.ui.orders.ordersAdapter

interface OrdersListener {
    fun onItemClick(productId: Int)
    fun onOrderDetailClick(orderId: Long)
}
