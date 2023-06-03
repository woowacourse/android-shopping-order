package woowacourse.shopping

data class OrderProducts(private val list: List<OrderProduct>) {
    private val _items = list.toMutableList()
    val items get() = _items.toList()
    val totalPrice get() = items.sumOf { it.totalPrice }
}
