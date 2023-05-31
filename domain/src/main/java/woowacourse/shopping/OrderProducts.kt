package woowacourse.shopping

class OrderProducts(list: List<OrderProduct>) {
    private val _items = list.toMutableList()
    val items get() = _items.toList()
    val totalPrice get() = items.sumOf { it.totalPrice }
}
