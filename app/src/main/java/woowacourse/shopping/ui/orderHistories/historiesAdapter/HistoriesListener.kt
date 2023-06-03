package woowacourse.shopping.ui.orderHistories.historiesAdapter

interface HistoriesListener {
    fun onItemClick(productId: Int)
    fun onOrderDetailClick(orderId: Long)
}
